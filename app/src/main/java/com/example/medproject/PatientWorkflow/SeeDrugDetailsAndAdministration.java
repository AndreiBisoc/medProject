package com.example.medproject.PatientWorkflow;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.data.Models.Drug;
import com.example.medproject.data.Models.DrugAdministration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class SeeDrugDetailsAndAdministration extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase mFirebaseDatabase;
    private TextInputEditText txtScop, txtUnitate, txtDescriere;
    private TextInputEditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private String drugAdministrationID;
    private boolean canEditMedicationFlag;
    private Locale locale = Locale.forLanguageTag("ro_RO");

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.drug_details_administration);

        ConstraintLayout container = findViewById(R.id.container1);
        BasicActions.hideKeyboardWithClick(container, this);

        txtScop = findViewById(R.id.txtScop);
        txtUnitate = findViewById(R.id.txtUnitate);
        txtDescriere = findViewById(R.id.txtDescriere);

        txtDosage = findViewById(R.id.txtDosage);
        txtNoOfDays = findViewById(R.id.txtNoOfDays);
        txtNoOfTimes = findViewById(R.id.txtNoOfTimes);
        txtStartDay = findViewById(R.id.txtStartDay);
        txtStartHour = findViewById(R.id.txtStartHour);

        Intent intent = getIntent();
        String drugID = intent.getStringExtra("drugID");
        drugAdministrationID = intent.getStringExtra("drugAdministrationID");
        canEditMedicationFlag = intent.getBooleanExtra("canEditMedicationFlag", false);
        boolean loggedAsDoctor = intent.getBooleanExtra("loggedAsDoctor", false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Drugs/" + drugID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Drug drug = dataSnapshot.getValue(Drug.class);
                setBasicDrugDetails(drug);
                setTitle(drug.getNume());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("DrugAdministration/" + drugAdministrationID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DrugAdministration drugAdministration = dataSnapshot.getValue(DrugAdministration.class);
                txtDosage.setText(drugAdministration.getDosage());
                txtNoOfDays.setText(drugAdministration.getNoOfDays());
                if(!txtNoOfTimes.getText().toString().contains("/zi") && !drugAdministration.getNoOfTimes().contains("/zi")){
                    txtNoOfTimes.setText(drugAdministration.getNoOfTimes() + "/zi");
                }
                else{
                    txtNoOfTimes.setText(drugAdministration.getNoOfTimes());
                }

                txtStartDay.setText(drugAdministration.getStartDay());
                txtStartHour.setText(drugAdministration.getStartHour());

                enableEditTexts(canEditMedicationFlag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button saveChanges = findViewById(R.id.saveChangesButton);
        if(!canEditMedicationFlag) {
            saveChanges.setVisibility(View.INVISIBLE);
        }
        saveChanges.setOnClickListener(v -> {
            DrugAdministration changedAdministration = new DrugAdministration();

            changedAdministration.setDosage(txtDosage.getText().toString().trim());
            changedAdministration.setNoOfDays(txtNoOfDays.getText().toString().trim());
            changedAdministration.setNoOfTimes(txtNoOfTimes.getText().toString().trim());
            changedAdministration.setStartDay(txtStartDay.getText().toString().trim());
            changedAdministration.setStartHour(txtStartHour.getText().toString().trim());

            // maybe here we should replace with updating a medication like we update the patient and doctor using updateChildren instead of setValue
            FirebaseDatabase.getInstance().getReference("DrugAdministration")
                    .child(drugAdministrationID)
                    .setValue(changedAdministration).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Medicația a fost editată cu succes");
                    finish();
                }
            });
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtStartDay:
                openDatePicker();
                break;
            case R.id.txtStartHour:
                openTimePicker();
                break;
        }
    }

    private void setBasicDrugDetails(Drug drug) {
        txtScop.setText(drug.getScop());
        txtUnitate.setText(drug.getUnitate());
        txtDescriere.setText(drug.getDescriere());
    }

    private void enableEditTexts(boolean canEditMedicationFlag){
        txtDosage.setFocusable(canEditMedicationFlag);
        txtNoOfDays.setFocusable(canEditMedicationFlag);
        txtNoOfTimes.setFocusable(canEditMedicationFlag);
        txtStartDay.setFocusable(canEditMedicationFlag);
        txtStartHour.setFocusable(canEditMedicationFlag);

        if(canEditMedicationFlag) {
            txtStartDay.setOnClickListener(this);
            txtStartHour.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        menu.removeItem(R.id.insert_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
        }
        return true;
    }

    private void openDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Date picker dialog
        DatePickerDialog datePickerDialog= new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String d = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    String m = monthOfYear < 10 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
                    txtStartDay.setText(String.format(locale, "%s/%s/%s", d, m, year1));
                }, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.setTitle("Selectați data de început");
        datePickerDialog.show();
    }

    private void openTimePicker(){
        // Time picker dialog
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                (view, hour, minute) -> {
                    String h = hour < 10 ? "0" + hour : "" + hour;
                    String m = minute < 10 ? "0" + minute : "" + minute;
                    txtStartHour.setText(String.format("%s:%s", h, m));
                }, 8, 0, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setTitle("Selectați ora de început");
        timePickerDialog.show();
    }

}

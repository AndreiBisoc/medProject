package com.example.medproject.PatientWorkflow.DrugDetails;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.DrugAdministration;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DrugDetailsAndAdministration extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private TextInputEditText txtScop, txtUnitate, txtDescriere;
    private TextInputEditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private String drugAdministrationID;
    private boolean canEditMedicationFlag;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.drug_details_administration);

        ScrollView container = findViewById(R.id.container);
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

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Drugs/" + drugID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Drug drug = dataSnapshot.getValue(Drug.class);
                setTitle(drug.getNume());
                txtScop.setText(drug.getScop());
                txtUnitate.setText(drug.getUnitate());
                txtDescriere.setText(drug.getDescriere());
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

    private void enableEditTexts(boolean canEditMedicationFlag){
        txtDosage.setEnabled(canEditMedicationFlag);
        txtNoOfDays.setEnabled(canEditMedicationFlag);
        txtNoOfTimes.setEnabled(canEditMedicationFlag);
        txtStartDay.setEnabled(canEditMedicationFlag);
        txtStartHour.setEnabled(canEditMedicationFlag);
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
        switch(item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
                break;
        }
        return true;
    }

}

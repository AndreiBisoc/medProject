package com.example.medproject.PatientWorkflow.DrugDetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.medproject.BasicActions;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.DrugAdministration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DrugDetailsAndAdministration extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private TextInputEditText txtScop, txtUnitate, txtDescriere;
    private TextInputEditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private String drugID, drugAdministrationID;
    private boolean canEditMedicationFlag;
    private Button saveChanges;
    private ScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_details_administration);

        container = findViewById(R.id.container);
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
        drugID = intent.getStringExtra("drugID");
        drugAdministrationID = intent.getStringExtra("drugAdministrationID");
        canEditMedicationFlag = intent.getBooleanExtra("canEditMedicationFlag", false);

        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Drugs/" + drugID);
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

        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("DrugAdministration/" + drugAdministrationID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DrugAdministration drugAdministration = dataSnapshot.getValue(DrugAdministration.class);
                txtDosage.setText(drugAdministration.getDosage());
                txtNoOfDays.setText(drugAdministration.getNoOfDays());
                txtNoOfTimes.setText(drugAdministration.getNoOfTimes());
                txtStartDay.setText(drugAdministration.getStartDay());
                txtStartHour.setText(drugAdministration.getStartHour());

                enableEditTexts(canEditMedicationFlag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveChanges = findViewById(R.id.saveChangesButton);
        if(!canEditMedicationFlag) {
            saveChanges.setVisibility(View.INVISIBLE);
        }
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrugAdministration changedAdministration = new DrugAdministration();

                changedAdministration.setDosage(txtDosage.getText().toString().trim());
                changedAdministration.setNoOfDays(txtNoOfDays.getText().toString().trim());
                changedAdministration.setNoOfTimes(txtNoOfTimes.getText().toString().trim());
                changedAdministration.setStartDay(txtStartDay.getText().toString().trim());
                changedAdministration.setStartHour(txtStartHour.getText().toString().trim());

                FirebaseDatabase.getInstance().getReference("DrugAdministration")
                        .child(drugAdministrationID)
                        .setValue(changedAdministration).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            BasicActions.displaySnackBar(getWindow().getDecorView(), "Medicația a fost editată cu succes");
                            finish();
                        }
                    }
                });
            }
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

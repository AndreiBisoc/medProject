package com.example.medproject.PatientWorkflow.DrugDetails;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medproject.R;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.DrugAdministration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DrugDetailsAndAdministration extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtNume, txtScop, txtUnitate, txtDescriere;
    EditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    String drugID, drugAdministrationID;
    Drug drug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_details_administration);


        txtNume = findViewById(R.id.txtNume);
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

        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Drugs/" + drugID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Drug drug = dataSnapshot.getValue(Drug.class);
                txtNume.setText("Nume: " + drug.getNume());
                txtScop.setText("Scop: " +drug.getScop());
                txtUnitate.setText("Unitate: " +drug.getUnitate());
                txtDescriere.setText("Descriere: " + drug.getDescriere());

                //enableEditTexts(false);
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
                txtDosage.setText("Dozaj: " + drugAdministration.getDosage());
                txtNoOfDays.setText("Numar zile: " + drugAdministration.getNoOfDays());
                txtNoOfTimes.setText("De cate ori pe zi: " + drugAdministration.getNoOfTimes());
                txtStartDay.setText("Zi de inceput: " + drugAdministration.getStartDay());
                txtStartHour.setText("Ora de inceput: " + drugAdministration.getStartHour());

                enableEditTexts(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableEditTexts(boolean isEnabled){
        txtNume.setEnabled(isEnabled);
        txtScop.setEnabled(isEnabled);
        txtUnitate.setEnabled(isEnabled);
        txtDescriere.setEnabled(isEnabled);
        txtDosage.setEnabled(isEnabled);
        txtNoOfDays.setEnabled(isEnabled);
        txtNoOfTimes.setEnabled(isEnabled);
        txtStartDay.setEnabled(isEnabled);
        txtStartHour.setEnabled(isEnabled);
    }

}
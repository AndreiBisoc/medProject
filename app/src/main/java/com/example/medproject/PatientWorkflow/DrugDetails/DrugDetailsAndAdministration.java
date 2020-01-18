package com.example.medproject.PatientWorkflow.DrugDetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private DatabaseReference mDatabaseReference;
    private TextInputEditText txtNume, txtScop, txtUnitate, txtDescriere;
    private TextInputEditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private String drugID, drugAdministrationID;
    private boolean canEditMedicationFlag;

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
        canEditMedicationFlag = intent.getBooleanExtra("canEditMedicationFlag", false);

        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Drugs/" + drugID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Drug drug = dataSnapshot.getValue(Drug.class);
                txtNume.setText(drug.getNume());
                txtScop.setText(drug.getScop());
                txtUnitate.setText(drug.getUnitate());
                txtDescriere.setText(drug.getDescriere());

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
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

}

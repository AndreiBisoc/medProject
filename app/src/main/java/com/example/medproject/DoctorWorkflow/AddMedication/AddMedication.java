package com.example.medproject.DoctorWorkflow.AddMedication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.DrugAdministration;
import com.example.medproject.data.model.DrugMedication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddMedication extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtDiagnostic, txtDrugName, txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    DrugMedication drugMedication;
    DrugAdministration drugAdministration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medication);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        txtDiagnostic = findViewById(R.id.txtDiagnostic);
        txtDrugName = findViewById(R.id.txtDrugName);
        txtDosage = findViewById(R.id.txtDosage);
        txtNoOfDays = findViewById(R.id.txtNoOfDays);
        txtNoOfTimes = findViewById(R.id.txtNoOfTimes);
        txtStartDay = findViewById(R.id.txtStartDay);
        txtStartHour = findViewById(R.id.txtStartHour);

        drugMedication = new DrugMedication();
        drugAdministration = new DrugAdministration();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveMedication();
                Toast.makeText(this, "Medicamentul a fost salvat", Toast.LENGTH_LONG).show();
                clean();
                backToMedications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMedication(){

        drugAdministration.setDosage(txtDosage.getText().toString());
        drugAdministration.setNoOfDays(txtNoOfDays.getText().toString());
        drugAdministration.setNoOfTimes(txtNoOfTimes.getText().toString());
        drugAdministration.setStartDay(txtStartDay.getText().toString());
        drugAdministration.setStartHour(txtStartHour.getText().toString());

        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        mDatabaseReference.push().setValue(drugAdministration);

        drugMedication.setDrugAdministrationID(drugAdministration.getID());
        drugMedication.setDiagnostic(txtDiagnostic.getText().toString());
        drugMedication.setDrugName(txtDrugName.getText().toString());

        //get Doctor's name
        String doctorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference  = mFirebaseDatabase.getReference("Doctors/" + doctorID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                drugMedication.setDoctorName(doctor.getLastName() + doctor.getFirstName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get drug's name
        mDatabaseReference  = mFirebaseDatabase.getReference("Drugs");
        Query query = mDatabaseReference.orderByChild("nume").equalTo(drugMedication.getDrugName());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("Key: ", key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

        String drugAdministrationID = mDatabaseReference.push().getKey();
        drugMedication.setDrugAdministrationID(drugAdministrationID);
        mDatabaseReference = mFirebaseDatabase.getReference("Medications/" + drugMedication.getID());
        mDatabaseReference.push().setValue(drugMedication);
    }


    private void backToMedications(){
        finish();
        Intent intent = new Intent(this, MyMedications.class);
        intent.putExtra("patientId", "IDsecret");
        startActivity(intent);
    }

    private void clean() {
        txtDiagnostic.setText("");
        txtDrugName.setText("");
        txtDosage.setText("");
        txtNoOfDays.setText("");
        txtNoOfTimes.setText("");
        txtStartDay.setText("");
        txtStartHour.setText("");
    }
}

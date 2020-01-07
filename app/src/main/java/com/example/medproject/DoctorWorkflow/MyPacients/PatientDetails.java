package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PatientDetails extends AppCompatActivity {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtAddress;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtCNP = findViewById(R.id.txtCNP);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);

        Intent intent = getIntent();
        String patientID = intent.getStringExtra("patientID");


        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Patients/" + patientID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                txtLastname.setText(patient.getLastName());
                txtFirstname.setText(patient.getFirstName());
                txtCNP.setText(patient.getCNP());
                txtBirthDate.setText(patient.getBirthDate());
                txtPhone.setText(patient.getPhone());
                txtAddress.setText(patient.getAddress());
                enableEditTexts(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableEditTexts(boolean isEnabled){
        txtLastname.setEnabled(isEnabled);
        txtFirstname.setEnabled(isEnabled);
        txtCNP.setEnabled(isEnabled);
        txtBirthDate.setEnabled(isEnabled);
        txtPhone.setEnabled(isEnabled);
        txtAddress.setEnabled(isEnabled);
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
            case R.id.edit_account:
                startActivity(new Intent(this, DoctorDetails.class));
                break;
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

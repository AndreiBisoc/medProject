package com.example.medproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PatientDetails extends AppCompatActivity {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtEmail, txtAddress;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);


        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstname);
        txtCNP = findViewById(R.id.txtCNP);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtAddress = findViewById(R.id.txtAddress);

        Intent intent = getIntent();
        String patientID = intent.getStringExtra("patientID");


        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Patients/" + patientID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                txtLastname.setText("Nume: " + patient.getLastName());
                txtFirstname.setText("Prenume: " +patient.getFirstName());
                txtCNP.setText("CNP: " +patient.getCNP());
                txtBirthDate.setText("Data nasterii: " + patient.getBirthDate());
                txtPhone.setText("Telefon: " + patient.getPhone());
                txtEmail.setText("Email: " + patient.getEmail());
                txtAddress.setText("Adresa: " + patient.getAddress());

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
        txtEmail.setEnabled(isEnabled);
        txtAddress.setEnabled(isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);

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

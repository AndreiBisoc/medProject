package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.medproject.data.model.Doctor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DoctorRegisterActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtFistName;
    private EditText txtLastName;
    private Spinner spinnerSpecialization;
    private EditText txtPhone;
    private Button registerButton;
    Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Doctors");

        Intent intent = getIntent();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        txtFistName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        spinnerSpecialization = findViewById(R.id.specialization);
        txtPhone = findViewById(R.id.phone);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setEnabled(true);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                saveDoctorToDatabase(email, password);
                clean();
            }
        });
    }

    public void saveDoctorToDatabase(String email, String password){
        String firstName = txtFistName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String specialization = spinnerSpecialization.getSelectedItem().toString();
        String phone = txtPhone.getText().toString();

        doctor = new Doctor(email, password, firstName, lastName, specialization, phone);

        if(doctor.getId() == null) {
            mDatabaseReference.push().setValue(doctor);
        }
        else{
            mDatabaseReference.child(doctor.getId()).setValue(doctor);
        }

    }

    public void clean(){
        txtFistName.setText("");
        txtFistName.requestFocus();
        txtLastName.setText("");
        txtPhone.setText("");
    }
}

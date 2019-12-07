package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.medproject.data.model.Patient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PatientRegisterActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtFistName;
    private EditText txtLastName;
    private EditText txtBirthDate;
    private EditText txtAddress;
    private EditText txtPhone;
    private Button registerButton;
    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Patients");

        Intent intent = getIntent();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        txtFistName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        txtBirthDate = findViewById(R.id.birthDate);
        txtAddress = findViewById(R.id.address);
        txtPhone = findViewById(R.id.phone);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setEnabled(true);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                savePatientToDatabase(email, password);
                clean();
            }
        });
    }

    public void savePatientToDatabase(String email, String password){
        String firstName = txtFistName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String phone = txtPhone.getText().toString();

        Date birthDate = new Date();
        String  date = txtBirthDate.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            birthDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String address = txtAddress.getText().toString();
        patient = new Patient(email, password, firstName, lastName, birthDate, phone, address);

        if(patient.getId() == null) {
            mDatabaseReference.push().setValue(patient);
        }
        else{
            mDatabaseReference.child(patient.getId()).setValue(patient);
        }

    }

    public void clean(){
        txtFistName.setText("");
        txtFistName.requestFocus();
        txtLastName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtBirthDate.setText("");
    }
}

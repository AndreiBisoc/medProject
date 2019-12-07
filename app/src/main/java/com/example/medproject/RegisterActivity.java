package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtFistName;
    private EditText txtLastName;
    private Spinner spinnerSpecialisation;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtEmail;
    private EditText txtPhone;
    private Button registerButton;
    Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Doctors");

        txtFistName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        spinnerSpecialisation = findViewById(R.id.specialisation);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtEmail = findViewById(R.id.email);
        txtPhone = findViewById(R.id.phone);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setEnabled(true);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                saveDoctorToDatabase();
                clean();
            }
        });
    }

    public void addListenerOnSpinnerItemSelection() {
        spinnerSpecialisation = (Spinner) findViewById(R.id.specialisation);
        spinnerSpecialisation.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void saveDoctorToDatabase(){
        String firstName = txtFistName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String specialisation = spinnerSpecialisation.getSelectedItem().toString();
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();

        doctor = new Doctor(firstName, lastName, specialisation, username, password, email, phone);

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
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
    }
}

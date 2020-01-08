package com.example.medproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.MyPacients.MyPatientsActivity;
import com.example.medproject.R;
import com.example.medproject.data.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDoctorActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtPrenume, txtNume, txtTelefon, txtAdresaCabinet;
    private Spinner txtSpinnerSpecialization;
    private ProgressBar progressBar;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtPrenume = findViewById(R.id.email);
        txtNume = findViewById(R.id.password);
        txtTelefon = findViewById(R.id.patientPhoneNumber);
        txtAdresaCabinet = findViewById(R.id.address);
        txtSpinnerSpecialization = findViewById(R.id.specialization);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        registerButton.setEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            //handle the already loged in user
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerButton:
                registerUser();
                break;
        }
    }

    private void registerUser(){
        Intent intent = getIntent();

        final String prenume = txtPrenume.getText().toString().trim();
        final String nume = txtNume.getText().toString().trim();
        final String telefon = txtTelefon.getText().toString().trim();
        final String adresaCabinet = txtAdresaCabinet.getText().toString().trim();
        final String specialization = txtSpinnerSpecialization.getSelectedItem().toString();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        if(validareRegisterPacient(prenume, nume, telefon, adresaCabinet) == true){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Doctor doctor = new Doctor(email, prenume, nume, specialization, telefon, adresaCabinet);
                            doctor.setId(mAuth.getUid());
                            FirebaseDatabase.getInstance().getReference("Doctors")
                                    .child(mAuth.getUid())
                                    .setValue(doctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterDoctorActivity.this, "Înregistrarea a avut loc cu succes", Toast.LENGTH_LONG).show();
                                        finish();
                                        // aici ar trebui sa te duca la pagina de Details, dar inca nu e UI pt ea, deci am redirectat direct la lista de pacienti
                                        startActivity(new Intent(RegisterDoctorActivity.this, MyPatientsActivity.class));
                                    }
                                    else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) { //deja exista un user cu acest mail
                                            Toast.makeText(RegisterDoctorActivity.this, "Există deja un cont cu acest email", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterDoctorActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validareRegisterPacient(String prenume, String nume, String telefon, String adresaCabinet){

        if(prenume.isEmpty()){
            txtPrenume.setError("Introduceți prenumele");
            txtPrenume.requestFocus();
            return true;
        }

        if(nume.isEmpty()){
            txtNume.setError("Introduceți numele");
            txtNume.requestFocus();
            return true;
        }

        if(telefon.isEmpty()){
            txtTelefon.setError("Introduceți nr. de telefon");
            txtTelefon.requestFocus();
            return true;
        }
        if(telefon.length() < 10){
            txtTelefon.setError("Introduceți un nr. de telefon valid");
            txtTelefon.requestFocus();
            return true;
        }

        if(adresaCabinet.isEmpty()){
            txtAdresaCabinet.setError("Introduceți adresa");
            txtAdresaCabinet.requestFocus();
            return true;
        }
        return false;
    }
}

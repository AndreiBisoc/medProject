package com.example.medproject.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.R;
import com.example.medproject.data.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterPacientActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtPrenume, txtNume, txtCNP, txtTelefon, txtDataNastere, txtAdresa;
    private ProgressBar progressBar;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        txtPrenume = findViewById(R.id.firstName);
        txtNume = findViewById(R.id.lastName);
        txtCNP = findViewById(R.id.CNP);
        txtTelefon = findViewById(R.id.phone);
        txtDataNastere = findViewById(R.id.birthDate);
        txtAdresa = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        registerButton.setEnabled(true);

        txtDataNastere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();

                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RegisterPacientActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtDataNastere.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


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
        final String CNP = txtCNP.getText().toString().trim();
        final String telefon = txtTelefon.getText().toString().trim();
        final String dataNastere = txtDataNastere.getText().toString().trim();
        final String adresa = txtAdresa.getText().toString().trim();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        if(validareRegisterPacient(prenume, nume, CNP, dataNastere, telefon, adresa) == true){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Patient pacient = new Patient(email, prenume, nume, dataNastere, telefon, adresa, CNP);

                            FirebaseDatabase.getInstance().getReference("patients")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(pacient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterPacientActivity.this, "Înregistrarea a avut loc cu succes", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(RegisterPacientActivity.this, "Înregistrarea nu a putut avea loc", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterPacientActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private boolean validareRegisterPacient(String prenume, String nume, String CNP, String dataNastere, String telefon, String adresa){

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

        if(CNP.isEmpty()){
            txtCNP.setError("Introduceți CNP");
            txtCNP.requestFocus();
            return true;
        }
        if(CNP.length() != 13){
            txtCNP.setError("Introduceți un CNP valid");
            txtCNP.requestFocus();
            return true;
        }

        if(dataNastere.isEmpty()){
            txtDataNastere.setError("Introduceți data nașterii");
            txtDataNastere.requestFocus();
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

        if(adresa.isEmpty()){
            txtAdresa.setError("Introduceți adresa");
            txtAdresa.requestFocus();
            return true;
        }
        return false;
    }
}

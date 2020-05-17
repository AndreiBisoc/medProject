package com.example.medproject.Authentication.Register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.PatientWorkflow.MyMedications;
import com.example.medproject.R;
import com.example.medproject.Models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterPacientActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtPrenume, txtNume, txtCNP, txtTelefon, birthDateEditText, txtAdresa;
    private DatePicker txtDataNastere;
    private ProgressBar progressBar;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatePickerDialog picker;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_patient_register);
        BasicActions.hideActionBar(this);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtPrenume = findViewById(R.id.txtPrenume);
        txtNume = findViewById(R.id.txtNume);

        txtCNP = findViewById(R.id.patientCNP);
        txtCNP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 12)
                    hideKeyboard();
            }
        });

        txtTelefon = findViewById(R.id.patientPhoneNumber);
        txtTelefon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 10)
                    hideKeyboard();
            }
        });

        birthDateEditText = findViewById(R.id.birthDateEditText);
        birthDateEditText.setInputType(InputType.TYPE_NULL);
        birthDateEditText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(RegisterPacientActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> birthDateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
                picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                picker.setTitle("Selectați data nașterii");
            picker.show();
        });

        txtAdresa = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        registerButton.setEnabled(true);
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
        final String dataNastere = birthDateEditText.getText().toString().trim();
        final String adresa = txtAdresa.getText().toString().trim();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        if(validareRegisterPacient(prenume, nume, CNP, dataNastere, telefon, adresa)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Patient patient = new Patient(email, prenume, nume, dataNastere, telefon, adresa, CNP);
                        patient.setId(mAuth.getUid());
                        FirebaseDatabase.getInstance().getReference("Patients")
                                .child(mAuth.getUid())
                                .setValue(patient).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                BasicActions.displaySnackBar(getWindow().getDecorView(), "Înregistrarea a avut loc cu succes");
                                finishAffinity();
                                Intent intent1 = new Intent(RegisterPacientActivity.this, MyMedications.class);
                                intent1.putExtra("loggedAsDoctor", false);
                                startActivity(intent1);
                            } else {
                                if (task1.getException() instanceof FirebaseAuthUserCollisionException) { //deja exista un user cu acest mail
                                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Există deja un cont cu acest email");
                                } else {
                                    Toast.makeText(getApplicationContext(), task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterPacientActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            birthDateEditText.setError("Introduceți data nașterii");
            birthDateEditText.requestFocus();
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

    private void disableControllers(boolean isEnabled){
        txtNume.setEnabled(!isEnabled);
        txtPrenume.setEnabled(!isEnabled);
        txtAdresa.setEnabled(!isEnabled);
        txtTelefon.setEnabled(!isEnabled);
        txtCNP.setEnabled(!isEnabled);
        birthDateEditText.setEnabled(!isEnabled);
        registerButton.setEnabled(!isEnabled);
    }

    private void hideKeyboard() {
        BasicActions.hideKeyboard(this);
    }
}

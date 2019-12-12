package com.example.medproject.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.example.medproject.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText txtEmail, txtPassword;
    private RadioGroup radioGroup;
    private Button nextButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        radioGroup = findViewById(R.id.radioUserTypeGroup);
        radioGroup.clearCheck();

        nextButton = findViewById(R.id.registerButton);
        nextButton.setEnabled(true);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int checkedId = radioGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.isDoctorButton:
                goToNextRegisterPage("doctor");
                break;
            case R.id.isPatientButton:
                goToNextRegisterPage("patient");
                break;
            default:
                break;
        }
    }

    public void goToNextRegisterPage(String type){
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        Class nextPage;

        if(authValidation(email, password) == true){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        switch(type){
            case "doctor": nextPage = RegisterDoctorActivity.class;
                break;
            case "patient": nextPage = RegisterPacientActivity.class;
                break;
            default: nextPage = LoginActivity.class;
        }

        Intent intent = new Intent(this, nextPage);
        intent.putExtra("EMAIL", email);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);
    }

    private boolean authValidation(String email, String password){

        if(email.isEmpty()){
            txtEmail.setError("Introduceți adresa de email");
            txtEmail.requestFocus();
            return true;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Introduceți o adresă validă");
            txtEmail.requestFocus();
            return true;
        }

        if(password.isEmpty()){
            txtPassword.setError("Introduceți parola");
            txtPassword.requestFocus();
            return true;
        }
        if(password.length() < 6){
            txtPassword.setError("Introduceți minim 6 caractere");
            txtPassword.requestFocus();
            return true;
        }

        return false;
    }
}

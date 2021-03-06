package com.example.medproject.Authentication.Register;

import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText txtEmail, txtPassword;
    private Button registerButton;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_register);
        BasicActions.hideActionBar(this);

        Intent intentFromLogin = getIntent();
        String email = intentFromLogin.getStringExtra("email");
        String password = intentFromLogin.getStringExtra("password");

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(35) });
        if(email != null && password != null) {
            txtEmail.setText(email);
            txtPassword.setText(password);
        }

        progressBar = findViewById(R.id.progressBar);

        radioGroup = findViewById(R.id.rol);
        registerButton = findViewById(R.id.registerButton);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if(!isChecked)
            {
                setRegisterButtonStyle(false, "#ffe082");
            } else {
                setRegisterButtonStyle(true, "#ffb300");
            }
        });

        registerButton.setOnClickListener(this);
    }

    private void setRegisterButtonStyle(boolean isEnabled, String colorString) {
        registerButton.setEnabled(isEnabled);
        registerButton.setBackgroundColor(Color.parseColor(colorString));
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

    private void goToNextRegisterPage(String type){
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        Class nextPage;

        if(authValidation(email, password)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);
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

    private void disableControllers(boolean isEnabled){
        txtEmail.setEnabled(!isEnabled);
        txtPassword.setEnabled(!isEnabled);
        registerButton.setEnabled(!isEnabled);
        radioGroup.setEnabled(!isEnabled);
    }
}

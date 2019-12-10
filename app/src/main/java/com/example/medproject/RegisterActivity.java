package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.medproject.auth.RegisterPacientActivity;
import com.example.medproject.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtPassword;
    private EditText txtEmail;
    private RadioGroup radioGroup;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        radioGroup = findViewById(R.id.radioUserTypeGroup);
        radioGroup.clearCheck();
        nextButton = findViewById(R.id.registerButton);

        nextButton.setEnabled(true);
        nextButton.setOnClickListener(new View.OnClickListener() {

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
                clean();
            }
        });
    }


    public void clean(){
        txtPassword.setText("");
        txtEmail.setText("");
        radioGroup.clearCheck();
    }

    public void goToNextRegisterPage(String type){
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        Class nextPage;

        switch(type){
            case "doctor": nextPage = DoctorRegisterActivity.class;
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
}

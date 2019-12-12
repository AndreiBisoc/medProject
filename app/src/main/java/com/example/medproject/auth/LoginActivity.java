package com.example.medproject.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.ListActivity;
import com.example.medproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText txtEmail, txtPassword;
    ProgressBar progressBar;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);

    }

    private void userLogin(){
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();

        if(authValidation(email, password) == true){
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Bine ațti venit!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                userLogin();
                break;

            case R.id.registerButton:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
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

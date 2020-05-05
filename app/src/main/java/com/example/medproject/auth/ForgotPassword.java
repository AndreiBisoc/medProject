package com.example.medproject.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.medproject.BasicActions;
import com.example.medproject.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Mi-am uitat parola");

        // hiding keyboard when the container is clicked
        ConstraintLayout container = findViewById(R.id.container);
        BasicActions.hideKeyboardWithClick(container, this);
        
    }
}

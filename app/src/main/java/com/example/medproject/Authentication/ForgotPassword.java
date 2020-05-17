package com.example.medproject.Authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Mi-am uitat parola");

        // hiding keyboard when the container is clicked
        ConstraintLayout container = findViewById(R.id.container);
        BasicActions.hideKeyboardWithClick(container, this);

        txtEmail = findViewById(R.id.txtEmail);
        MaterialButton sendEmail_button = findViewById(R.id.send_email);
        sendEmail_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                finish();
                break;
            case R.id.send_email:
                sendEmail();
                break;
        }
    }

    private void sendEmail() {
        String email = txtEmail.getText().toString().trim();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("ro_RO");

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        BasicActions.displaySnackBarAndFinish(ForgotPassword.this, "Email-ul de recuperare a fost trimis");
                    }
                    else {
                        BasicActions.displaySnackBar(getWindow().getDecorView(), "Nu există un cont cu această adresă sau aceasta a fost blocată");
                    }
                });
    }
}

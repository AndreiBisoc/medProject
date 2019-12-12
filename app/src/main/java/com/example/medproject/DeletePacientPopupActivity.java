package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DeletePacientPopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_pacient_popup);
    }

    public void closePopup(View v) {

        Intent intent = new Intent(this, MyPatientsActivity.class);
        startActivity(intent);

    }
}

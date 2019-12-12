package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DeletePacientPopupActivity extends Activity {

    private TextView patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_pacient_popup);

        patientName = findViewById(R.id.patientName);
        patientName.setText("Adi M");

    }

    public void closePopup(View v) {

        Intent intent = new Intent(this, MyPatientsActivity.class);
        startActivity(intent);

    }
}

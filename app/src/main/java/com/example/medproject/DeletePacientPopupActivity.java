package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.medproject.data.model.Patient;

public class DeletePacientPopupActivity extends Activity {

    private TextView patientName, patientCNP, patientDateOfBirth, patientPhoneNumber, closePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_pacient_popup);

        Patient patient = (Patient) getIntent().getExtras().getSerializable("Patient");

        patientName = findViewById(R.id.patientName);
        patientName.setText(patient.getName());

        patientCNP = findViewById(R.id.patientCNP);
        patientCNP.setText(patient.getCNP());

        patientDateOfBirth = findViewById(R.id.birthDate);
        patientDateOfBirth.setText(patient.getBirthDate());

        patientPhoneNumber = findViewById(R.id.patientPhoneNumber);
        patientPhoneNumber.setText(patient.getPhone());

    }

    public void closePopup(View v) {

        Intent intent = new Intent(this, MyPatientsActivity.class);
        startActivity(intent);

    }
}

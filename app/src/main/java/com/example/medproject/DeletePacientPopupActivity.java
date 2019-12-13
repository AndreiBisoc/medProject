package com.example.medproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medproject.data.model.Patient;
import com.google.firebase.database.DatabaseReference;

public class DeletePacientPopupActivity extends Activity {

    private DatabaseReference mdatabaseReference;
    private Patient patient;
    private TextView patientName, patientCNP, patientDateOfBirth, patientPhoneNumber;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_pacient_popup);

        patient = (Patient) getIntent().getExtras().getSerializable("Patient");

        patientName = findViewById(R.id.patientName);
        patientName.setText(patient.getName());

        patientCNP = findViewById(R.id.patientCNP);
        patientCNP.setText(patient.getCNP());

        patientDateOfBirth = findViewById(R.id.birthDate);
        patientDateOfBirth.setText(patient.getBirthDate());

        patientPhoneNumber = findViewById(R.id.patientPhoneNumber);
        patientPhoneNumber.setText(patient.getPhone());

        deleteButton = findViewById(R.id.deletebtn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     deletePatient();
                }
        });
    }

    public void deletePatient(){
        if(patient == null){
            Toast.makeText(this,"Vă rugăm salvați pacientul înainte să îl ștergeți!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this,"Îți șterg pacientul, bă!", Toast.LENGTH_LONG).show();

        mdatabaseReference = FirebaseUtil.mDatabaseReference;
        mdatabaseReference.child(patient.getId()).removeValue();
        finish();
    }

    public void closePopup(View v) {

        finish();

    }
}

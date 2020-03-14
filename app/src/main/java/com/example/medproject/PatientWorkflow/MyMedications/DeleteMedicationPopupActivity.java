package com.example.medproject.PatientWorkflow.MyMedications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medproject.FirebaseUtil;
import com.example.medproject.R;
import com.example.medproject.data.model.Medication;
import com.google.firebase.database.DatabaseReference;

public class DeleteMedicationPopupActivity extends Activity {
    private Medication medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_medication_popup);

        medication = (Medication) getIntent().getExtras().getSerializable("Medication");

        TextView medicationDiagnostic = findViewById(R.id.medicationDiagnostic);
        medicationDiagnostic.setText(medication.getDiagnostic());

        TextView numeDoctor = findViewById(R.id.numeDoctor);
        numeDoctor.setText(medication.getDoctorName());

        Button deleteButton = findViewById(R.id.deletebtn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedication();
            }
        });
    }

    private void deleteMedication(){
        if(medication == null){
            Toast.makeText(this,"Vă rugăm salvați medicația înainte să o ștergeți!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this,"Îți șterg medicația, bă!", Toast.LENGTH_LONG).show();

        DatabaseReference mdatabaseReference = FirebaseUtil.mDatabaseReference;
        mdatabaseReference.child(medication.getId()).removeValue();
        finish();
    }

    public void closePopup(View v) {
        finish();
    }
}

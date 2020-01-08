package com.example.medproject.DoctorWorkflow.AddMedication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
public class AddMedication extends AppCompatActivity implements View.OnClickListener {
    private EditText txtDiagnostic;
    private Button addDrugToMedicationButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_medication);

        txtDiagnostic = findViewById(R.id.txtDiagnostic);
        addDrugToMedicationButton = findViewById(R.id.addDrugsToMedicationButton);
        cancelButton = findViewById(R.id.cancelButton);

        addDrugToMedicationButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addDrugsToMedicationButton:
                addDrugToMedication();
                break;
            case R.id.cancelButton:
                finish();
                Intent intent = getIntent();
                String patientId = intent.getStringExtra("patientId");
                intent = new Intent(this, MyMedications.class);
                intent.putExtra("patientId", patientId);
                startActivity(intent);
                break;
        }
    }

    private void addDrugToMedication(){
        String diagnostic = txtDiagnostic.getText().toString();
        if(validareDiagnostic(diagnostic) == true){
            return;
        }

        finish();
        Intent intent = new Intent(this, AddDrugToMedication.class);
        intent.putExtra("diagnostic", diagnostic);
        startActivity(intent);
    }

    private boolean validareDiagnostic(String diagnostic) {

        if (diagnostic.isEmpty()) {
            txtDiagnostic.setError("Introduceți diagnosticul");
            txtDiagnostic.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

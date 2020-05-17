package com.example.medproject.DoctorWorkflow.AddMedication;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.PatientWorkflow.MyMedications;
import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMedication extends AppCompatActivity implements View.OnClickListener {
    private EditText txtDiagnostic, txtPrescriptionDate;
    private ImageView addMedicationImage;
    private Button addDrugToMedicationButton, cancelButton;
    private ProgressBar progressBar;
    private String patientId, patientName;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.add_medication);

        BasicActions.hideActionBar(this);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        addMedicationImage = findViewById(R.id.addMedicationImage);
        txtPrescriptionDate = findViewById(R.id.txtPrescriptionDate);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        txtPrescriptionDate.setText(dateFormat.format(todayDate));

        progressBar = findViewById(R.id.progressBar);
        txtDiagnostic = findViewById(R.id.txtDiagnostic);
        addDrugToMedicationButton = findViewById(R.id.addDrugsToMedicationButton);
        cancelButton = findViewById(R.id.cancelButton);
        addDrugToMedicationButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        String doctorId = FirebaseAuth.getInstance().getUid();
        getPatientDetails();

//        try {
//            BasicActions.checkDoctorPatientLink(doctorId, patientId);
//        } catch (DoctorNotLinkedToPatientException e) {
//            BasicActions.displaySnackBar(getWindow().getDecorView(), e.toString());
//        }

    }

    private void getPatientDetails() {
        Intent intent = getIntent();
        patientId = intent.getStringExtra("patientId");
        patientName = intent.getStringExtra("patientName");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addDrugsToMedicationButton:
                addDrugToMedication();
                break;
            case R.id.cancelButton:
                finish();
                Intent intent = new Intent(this, MyMedications.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("patientName", patientName);
                intent.putExtra("loggedAsDoctor", true);
                startActivity(intent);
                break;
        }
    }

    private void addDrugToMedication(){
        String diagnostic = txtDiagnostic.getText().toString();
        if(validareDiagnostic(diagnostic)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        finish();
        Intent intent = new Intent(this, AddDrugToMedication.class);
        intent.putExtra("diagnostic", diagnostic);
        intent.putExtra("patientId", patientId);
        startActivity(intent);
    }

    private boolean validareDiagnostic(String diagnostic) {

        if (diagnostic.isEmpty()) {
            txtDiagnostic.setError("Introduceți numele diagnosticului");
            txtDiagnostic.requestFocus();
            return true;
        }
        return false;
    }

    private void disableControllers(boolean isEnabled){
        txtDiagnostic.setEnabled(!isEnabled);
        addDrugToMedicationButton.setEnabled(!isEnabled);
        cancelButton.setEnabled(!isEnabled);
    }
}

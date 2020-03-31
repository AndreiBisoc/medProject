package com.example.medproject.DoctorWorkflow.AddMedication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.QRCode.GenerateQRCode;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.DrugAdministration;
import com.example.medproject.data.model.MedicationLink;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDrugToMedication extends AppCompatActivity implements View.OnClickListener {
    private EditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private AutoCompleteTextView searchDrugName;
    private TextView noOfInsertedDrugs;

    private final List<String> drugs = new ArrayList<>();
    private final List<String> drugIDs = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private MedicationLink medicationLink = new MedicationLink();
    private final ArrayList<MedicationLink> medicationLinkList = new ArrayList<>();
    private DrugAdministration drugAdministration = new DrugAdministration();
    private final ArrayList<DrugAdministration> drugAdministrationList = new ArrayList<>();
    private final ArrayList<String> medicationDrugIDs = new ArrayList<>();
    private String diagnostic;
    private String drugName;
    private String doctorName;
    private static int noOfDrugs = 0;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_drug_to_medication);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        progressBar = findViewById(R.id.progressBar);
        txtDosage = findViewById(R.id.txtDosage);
        txtNoOfDays = findViewById(R.id.txtNoOfDays);
        txtNoOfTimes = findViewById(R.id.txtNoOfTimes);
        txtStartDay = findViewById(R.id.txtStartDay);
        txtStartHour = findViewById(R.id.txtStartHour);
        Button addAnotherDrugButton = findViewById(R.id.addDrugButton);
        Button saveMedicationButton = findViewById(R.id.saveMedicationButton);
        noOfInsertedDrugs = findViewById(R.id.noOfInsertedDrugs);

        if (noOfDrugs == 0) {
            noOfInsertedDrugs.setText("Niciun medicament asociat");
        } else {
            noOfInsertedDrugs.setText("Aveți adăugate " + noOfDrugs + " medicamente");
        }

        addAnotherDrugButton.setOnClickListener(this);
        saveMedicationButton.setOnClickListener(this);

        //Creating the instance of ArrayAdapter containing list of CNPs
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, drugs);
        searchDrugName = findViewById(R.id.searchDrug);
        searchDrugName.setThreshold(1); // will start working from first character
        searchDrugName.setAdapter(adapter); // setting the adapter data into the AutoCompleteTextView

        Intent newDrugIntent = getIntent();
        String drugToAdd = newDrugIntent.getStringExtra("drugId");
        if(drugToAdd != null) {
            finishAddingDrug(drugToAdd);
        }


        Intent intent = getIntent();
        diagnostic = intent.getStringExtra("diagnostic");
        setTitle(diagnostic);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("Drugs")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Drug drug = dataSnapshot.getValue(Drug.class);
                        drugs.add(drug.getNume());
                        drugIDs.add(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//        Intent newDrugIntent = getIntent();
//        Drug drugToAdd = (Drug) newDrugIntent.getSerializableExtra("drug");
//        if(drugToAdd != null) {
//            drugID = drugIDs.get(drugs.indexOf(drugToAdd.getNume()));
//            finishAddingDrug();
//        }

        //get Doctor's name
        String doctorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = mFirebaseDatabase.getReference("Doctors/" + doctorID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                doctorName = doctor.getLastName() + " " + doctor.getFirstName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDrugButton:
                addDrugToMedication();
                break;
            case R.id.saveMedicationButton:
                saveMedication();
                break;
        }
    }

    private void saveMedication() {
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        // in cazul adaugarii unei medicatii cu un medicament recent adaugat, medicatia va contine doar numele doctorului in BD
        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        for (DrugAdministration drugAdms : drugAdministrationList) {
            mDatabaseReference.child(drugAdms.getID()).setValue(drugAdms);
        }

        String medicationLinkId = mDatabaseReference.push().getKey();
        mDatabaseReference = mFirebaseDatabase.getReference("Medications/" + medicationLinkId);
        for (int i = 0; i < medicationLinkList.size(); i++) {
            mDatabaseReference.child(medicationDrugIDs.get(i)).setValue(medicationLinkList.get(i));
        }
        mDatabaseReference.child("diagnostic").setValue(diagnostic);
        mDatabaseReference.child("doctorName").setValue(doctorName);

        finish();
        getDataForQRCode(medicationLinkId);

    }

    private void getDataForQRCode(String medicationLinkId) {
        Intent intentToGenerateQR = new Intent(this, GenerateQRCode.class);
        intentToGenerateQR.putExtra("medicationId", medicationLinkId);

        Intent intentFromAddMedication = getIntent();
        String patientId = intentFromAddMedication.getStringExtra("patientId");
        intentToGenerateQR.putExtra("patientId", patientId);

        startActivity(intentToGenerateQR);
    }

    private void addDrugToMedication() {
        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        drugAdministration = new DrugAdministration();
        medicationLink = new MedicationLink();
        drugAdministration.setID(mDatabaseReference.push().getKey());
        drugAdministration.setDosage(txtDosage.getText().toString().trim());
        drugAdministration.setNoOfDays(txtNoOfDays.getText().toString().trim());
        drugAdministration.setStartDay(txtStartDay.getText().toString().trim());
        drugAdministration.setStartHour(txtStartHour.getText().toString().trim());
        drugAdministration.setNoOfTimes(txtNoOfTimes.getText().toString().trim());
        drugAdministrationList.add(drugAdministration);

        if(validareDrugAdministration(drugAdministration)){
            return;
        }
        drugName = searchDrugName.getText().toString().trim();
        try {
            String drugID = drugIDs.get(drugs.indexOf(drugName));
            finishAddingDrug(drugID);

        } catch (Exception e) {
            searchDrugName.setError("Acest medicament nu există");
            searchDrugName.requestFocus();
            return;

//            new MaterialAlertDialogBuilder(getWindow().getDecorView().getContext())
//                    .setMessage("Medicamentul " + drugName + " nu există în baza de date. Doriți să îl adăugați?")
//                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intentToAddDrug = new Intent(AddDrugToMedication.this, AddDrug.class);
//                            intentToAddDrug.putExtra("drugName", drugName);
//                            startActivity(intentToAddDrug);
//                        }
//                    })
//                    .setNegativeButton("Nu", /* listener = */ null)
//                    .show();
        }
    }

    private void finishAddingDrug(String drugToAdd){
        medicationDrugIDs.add(drugToAdd);
        medicationLink.setDrugName(drugName);
        medicationLink.setDrugAdministration(drugAdministration.getID());
        medicationLinkList.add(medicationLink);

        clean();
//        Toast.makeText(this, "Ați adăugat " + drugName, Toast.LENGTH_LONG).show();
        noOfDrugs++;
        noOfInsertedDrugs.setText("Ați adăugat până acum " + noOfDrugs + " medicamente.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        progressBar.setVisibility(View.GONE);
        disableControllers(false);
    }

    private void clean() {
        searchDrugName.setText("");
        txtDosage.setText("");
        txtNoOfDays.setText("");
        txtNoOfTimes.setText("");
        txtStartDay.setText("");
        txtStartHour.setText("");
    }

    private boolean validareDrugAdministration(DrugAdministration drugAdministration){
        if(drugAdministration.getDosage().isEmpty()){
            txtDosage.setError("Introduceți dozaj");
            txtDosage.requestFocus();
            return true;
        }

        if(drugAdministration.getNoOfDays().isEmpty()){
            txtNoOfDays.setError("Introduceți nr. de zile");
            txtNoOfDays.requestFocus();
            return true;
        }

        if(drugAdministration.getNoOfTimes().isEmpty()){
            txtNoOfTimes.setError("Introduceți nr. de dăți");
            txtNoOfTimes.requestFocus();
            return true;
        }

        if(drugAdministration.getStartDay().isEmpty()){
            txtStartDay.setError("Introduceți data");
            txtStartDay.requestFocus();
            return true;
        }

        if(drugAdministration.getStartHour().isEmpty()){
            txtStartHour.setError("Introduceți ora");
            txtStartHour.requestFocus();
            return true;
        }
        return false;
    }

    private void disableControllers(boolean isEnabled){
        searchDrugName.setEnabled(!isEnabled);
        txtDosage.setEnabled(!isEnabled);
        txtNoOfDays.setEnabled(!isEnabled);
        txtNoOfTimes.setEnabled(!isEnabled);
        txtStartDay.setEnabled(!isEnabled);
        txtStartHour.setEnabled(!isEnabled);
    }
}

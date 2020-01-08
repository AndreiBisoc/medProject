package com.example.medproject.DoctorWorkflow.AddMedication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.QRCode.GenerateQRCode;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.DrugAdministration;
import com.example.medproject.data.model.Medication;
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

public class AddDrugToMedication extends AppCompatActivity implements View.OnClickListener{
    private EditText txtDosage, txtNoOfDays, txtNoOfTimes, txtStartDay, txtStartHour;
    private AutoCompleteTextView searchDrugName;
    private Button addAnotherDrugButton, saveMedicationButton;

    private List<String> drugs = new ArrayList<>();
    private List<String> drugIDs = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private MedicationLink medicationLink = new MedicationLink();
    private ArrayList<MedicationLink> medicationLinkList = new ArrayList<>();
    private DrugAdministration drugAdministration = new DrugAdministration();
    private ArrayList<DrugAdministration> drugAdministrationList = new ArrayList<>();
    private ArrayList<String> medicationDrugIDs = new ArrayList<>();
    private String diagnostic;
    private String drugName, drugID, doctorID, doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_drug_to_medication);

        txtDosage = findViewById(R.id.txtDosage);
        txtNoOfDays = findViewById(R.id.txtNoOfDays);
        txtNoOfTimes = findViewById(R.id.txtNoOfTimes);
        txtStartDay = findViewById(R.id.txtStartDay);
        txtStartHour = findViewById(R.id.txtStartHour);
        addAnotherDrugButton = findViewById(R.id.addDrugButton);
        saveMedicationButton = findViewById(R.id.saveMedicationButton);
        addAnotherDrugButton.setOnClickListener(this);
        saveMedicationButton.setOnClickListener(this);

        //Creating the instance of ArrayAdapter containing list of CNPs
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, drugs);
        searchDrugName = findViewById(R.id.searchDrug);
        searchDrugName.setThreshold(1); // will start working from first character
        searchDrugName.setAdapter(adapter); // setting the adapter data into the AutoCompleteTextView

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

        //get Doctor's name
        doctorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        Intent intent = getIntent();
        diagnostic = intent.getStringExtra("diagnostic");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addDrugButton:
                addDrugToMedication();
                break;
            case R.id.saveMedicationButton:
                saveMedication();
                break;
        }
    }

    private void saveMedication(){
        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        for (DrugAdministration drugAdms : drugAdministrationList) {
            mDatabaseReference.child(drugAdms.getID()).setValue(drugAdms);
        }

        String medicationLinkId = mDatabaseReference.push().getKey();
        mDatabaseReference = mFirebaseDatabase.getReference("Medications/" + medicationLinkId);
        for(int i=0; i<medicationLinkList.size(); i++){
            mDatabaseReference.child(medicationDrugIDs.get(i)).setValue(medicationLinkList.get(i));
        }
        mDatabaseReference.child("diagnostic").setValue(diagnostic);
        mDatabaseReference.child("doctorName").setValue(doctorName);

        Intent intent = new Intent(this, GenerateQRCode.class);
        intent.putExtra("medicationId", medicationLinkId);
        startActivity(intent);
    }
    
    private void addDrugToMedication(){
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

        drugName = searchDrugName.getText().toString().trim();
        drugID = drugIDs.get(drugs.indexOf(drugName));
        medicationDrugIDs.add(drugID);

        medicationLink.setDrugName(drugName);
        medicationLink.setDrugAdministration(drugAdministration.getID());
        medicationLinkList.add(medicationLink);

        clean();
        Toast.makeText(this, "Ați adăugat " + drugName + "\nAveți " + drugAdministrationList.size() + " medicamente adăugate", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void clean() {
        searchDrugName.setText("");
        txtDosage.setText("");
        txtNoOfDays.setText("");
        txtNoOfTimes.setText("");
        txtStartDay.setText("");
        txtStartHour.setText("");
    }
}

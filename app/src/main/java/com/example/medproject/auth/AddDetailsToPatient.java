package com.example.medproject.auth;

import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.R;
import com.example.medproject.data.model.Contact;
import com.example.medproject.data.model.Exceptions.NotLoggedAsPatientException;
import com.example.medproject.data.model.Patient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddDetailsToPatient extends AppCompatActivity{

    private AutoCompleteTextView chooseBloodType;
    private AutoCompleteTextView chooseRHType;
    private EditText age;
    private RadioGroup radioGroup;
    private DatabaseReference patientRef;
    private Button saveDetails;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_add_details_to_patient);
        BasicActions.hideActionBar(this);

//        hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        getBirthDateAndComputeAge();
        initializeDropDowns();

        radioGroup = findViewById(R.id.gender);
        saveDetails = findViewById(R.id.saveChangesButton);
        saveDetails.setEnabled(true);
        saveDetails.setOnClickListener(v -> setEmergencyDetails());

    }

    private void initializeDropDowns() {
        List<String> bloodTypes = new ArrayList<>( Arrays.asList("0 (I)", "A (II)", "B (III)", "AB (IV)"));
        ArrayAdapter< String > adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, bloodTypes);
        chooseBloodType =  findViewById(R.id.bloodType);
        chooseBloodType.setOnClickListener(v -> hideKeyboard());
        chooseBloodType.setAdapter(adapter);


        List<String> RHTypes = new ArrayList<>( Arrays.asList("RH+", "RH-"));
        ArrayAdapter< String > RHAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, RHTypes);
        chooseRHType =  findViewById(R.id.RHType);
        chooseRHType.setOnClickListener(v -> hideKeyboard());
        chooseRHType.setAdapter(RHAdapter);
    }

    private void getBirthDateAndComputeAge() {
        String loggedUser = FirebaseAuth.getInstance().getUid();
        patientRef = FirebaseDatabase.getInstance().getReference("Patients").child(loggedUser);
        age = findViewById(R.id.age);

        ValueEventListener birthDateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    try {
                        String birthDate = dataSnapshot.getValue(String.class);
                        int v = Patient.getAge(birthDate);
                        age.setText(Integer.toString(v));
                    } catch (NotLoggedAsPatientException e) {
                        BasicActions.displaySnackBar(getWindow().getDecorView(), e.toString());
                        saveDetails.setEnabled(false);
                        saveDetails.setBackgroundColor(Color.parseColor("#ffe082"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        patientRef.child("birthDate").addValueEventListener(birthDateListener);
    }

    private void setEmergencyDetails() {

        String gender = radioGroup.getCheckedRadioButtonId() == R.id.isMale ? "M" : "F";
        String bloodType = chooseBloodType.getText().toString().trim();
        String RHType = chooseRHType.getText().toString().trim();
        String allergies = ((TextInputEditText) findViewById(R.id.allergies)).getText().toString();
        String emergencyContactName = ((TextInputEditText) findViewById(R.id.emergencyContactName)).getText().toString();
        String emergencyContactPhone = ((TextInputEditText) findViewById(R.id.emergencyContactPhone)).getText().toString();
        String emergencyContactRelationship = ((TextInputEditText) findViewById(R.id.emergencyContactRelationship)).getText().toString();

        Patient p = new Patient();
        Contact emergencyContact = new Contact(emergencyContactName, emergencyContactPhone, emergencyContactRelationship);
        Map<String, Object> emergencyDetails = p.setEmergencyDetails(gender, bloodType, RHType, allergies, emergencyContact);

        patientRef.updateChildren(emergencyDetails);
    }

    private void hideKeyboard() {
        BasicActions.hideKeyboard(this);
    }
}

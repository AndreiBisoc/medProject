package com.example.medproject.MyAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.example.medproject.Models.Contact;
import com.example.medproject.Models.Patient;
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
import java.util.Objects;


public class PatientEmergencyDetails_Fragment extends Fragment {
    private AutoCompleteTextView chooseBloodType;
    private AutoCompleteTextView chooseRHType;
    private EditText age;
    private RadioGroup radioGroup;
    private DatabaseReference patientRef;
    private View myView;
    private ProgressBar progressBar;
    private TextInputEditText allergies, emergencyContactName, emergencyContactPhone, emergencyContactRelationship;

    public PatientEmergencyDetails_Fragment() {// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_emergency_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        myView = getView();
        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(requireView().findViewById(R.id.container), (AppCompatActivity) getActivity());
        final String loggedUser = FirebaseAuth.getInstance().getUid();

        initializeDropDowns();
        progressBar = myView.findViewById(R.id.progressBar);
        assert loggedUser != null;
        patientRef = FirebaseDatabase.getInstance().getReference("Patients").child(loggedUser);
        age = myView.findViewById(R.id.age);
        radioGroup = myView.findViewById(R.id.gender);
        allergies = myView.findViewById(R.id.allergies);
        emergencyContactName = myView.findViewById(R.id.emergencyContactName);
        emergencyContactPhone = myView.findViewById(R.id.emergencyContactPhone);
        emergencyContactRelationship = myView.findViewById(R.id.emergencyContactRelationship);
        displayEmergencyDetails();
        Button saveDetails = myView.findViewById(R.id.saveChangesButton);
        saveDetails.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            setEmergencyDetails();
        });
    }

    private void initializeDropDowns() {
        List<String> bloodTypes = new ArrayList<>( Arrays.asList("0 (I)", "A (II)", "B (III)", "AB (IV)"));
        ArrayAdapter adapter = new ArrayAdapter< >
                (this.requireContext(), android.R.layout.select_dialog_item, bloodTypes);
        chooseBloodType = myView.findViewById(R.id.bloodType);
        chooseBloodType.setOnClickListener(v -> hideKeyboard());
        chooseBloodType.setAdapter(adapter);


        List<String> RHTypes = new ArrayList<>( Arrays.asList("RH+", "RH-"));
        ArrayAdapter< String > RHAdapter = new ArrayAdapter<>
                (this.requireContext(), android.R.layout.select_dialog_item, RHTypes);
        chooseRHType =  myView.findViewById(R.id.RHType);
        chooseRHType.setOnClickListener(v -> hideKeyboard());
        chooseRHType.setAdapter(RHAdapter);
    }

    private void displayEmergencyDetails() {
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                assert patient != null;
                String birthDate = patient.getBirthDate();
                age.setText(Integer.toString(Patient.getAge(birthDate)));
                if(patient.getGender()!=null && patient.getGender().equals("F")) {
                    radioGroup.check(R.id.isFemale);
                } else {
                    radioGroup.check(R.id.isMale);
                }
                chooseBloodType.setText(patient.getBloodType());
                chooseRHType.setText(patient.getRH());
                allergies.setText(patient.getAllergies());
                if(patient.getEmergencyContact()!=null) {
                    emergencyContactName.setText(patient.getEmergencyContact().getName());
                    emergencyContactPhone.setText(patient.getEmergencyContact().getPhoneNumber());
                    emergencyContactRelationship.setText(patient.getEmergencyContact().getRelationship());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setEmergencyDetails() {

        String gender = radioGroup.getCheckedRadioButtonId() == R.id.isMale ? "M" : "F";
        String bloodType = chooseBloodType.getText().toString().trim();
        String RHType = chooseRHType.getText().toString().trim();
        String allergies = Objects.requireNonNull(((TextInputEditText) myView.findViewById(R.id.allergies)).getText()).toString();
        String emergencyContactName = Objects.requireNonNull(((TextInputEditText) myView.findViewById(R.id.emergencyContactName)).getText()).toString();
        String emergencyContactPhone = Objects.requireNonNull(((TextInputEditText) myView.findViewById(R.id.emergencyContactPhone)).getText()).toString();
        String emergencyContactRelationship = Objects.requireNonNull(((TextInputEditText) myView.findViewById(R.id.emergencyContactRelationship)).getText()).toString();

        Patient p = new Patient();
        Contact emergencyContact = new Contact(emergencyContactPhone, emergencyContactName, emergencyContactRelationship);
        Map<String, Object> emergencyDetails = p.setEmergencyDetails(gender, bloodType, RHType, allergies, emergencyContact);

        patientRef.updateChildren(emergencyDetails);
        BasicActions.displaySnackBar(requireActivity().getWindow().getDecorView(), "Datele dumneavoastră au fost salvate.");
        progressBar.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        BasicActions.hideKeyboard((AppCompatActivity) this.requireActivity());
    }
}

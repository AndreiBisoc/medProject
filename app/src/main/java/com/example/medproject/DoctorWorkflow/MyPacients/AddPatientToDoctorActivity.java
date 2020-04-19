package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.R;
import com.example.medproject.data.model.DoctorToPatientLink;
import com.example.medproject.data.model.Patient;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPatientToDoctorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseReferenceToPacients;
    private final List<String> CNPs = new ArrayList<>();
    private AutoCompleteTextView searchForCNP;
    private int length = 0;
    private ProgressBar progressBar;
    private Button addPatient;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_add_patient);
        BasicActions.hideActionBar(this);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        mAuth = FirebaseAuth.getInstance();

        addPatient = findViewById(R.id.addPatientButton);

        firebaseReferenceToPacients = FirebaseDatabase.getInstance().getReference("Patients");
        firebaseReferenceToPacients.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Patient patient = dataSnapshot.getValue(Patient.class);
                String cnp = patient.getCNP();
                CNPs.add(cnp);
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

        //Creating the instance of ArrayAdapter containing list of CNPs
        ArrayAdapter < String > adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, CNPs);

        progressBar = findViewById(R.id.progressBar);
        searchForCNP = findViewById(R.id.searchForCNP);
        searchForCNP.setAdapter(adapter); // setting the adapter data into the AutoCompleteTextView
        searchForCNP.setOnClickListener(v -> {
            if(length==0) {
                searchForCNP.dismissDropDown();
            }
        });
        searchForCNP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                length = s.toString().length();

                if (length > 12) {
                    hideKeyboard();
                }
            }
        });

        addPatient.setOnClickListener(v -> {
            final String searchedCNP = searchForCNP.getText().toString().trim();
            addPatientToDoctor(searchedCNP);
        });
    }

    private void addPatientToDoctor(final String searchedCNP) {
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        final String doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseReferenceToPacients.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                patient.setId(dataSnapshot.getKey());
                Patient patientToAdd = new Patient(patient.getFirstName(), patient.getLastName(), patient.getBirthDate(), patient.getPhone(),patient.getCNP());
                if(patient.getCNP().equals(searchedCNP)) {
                    String registerDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
                    DoctorToPatientLink link = new DoctorToPatientLink(patientToAdd, registerDate);
                    FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                            .child(doctorUid)
                            .child(patient.getId())
                            .setValue(link).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Pacientul a fost adăugat cu succes");
                                    finish();
                                }
                                else{
                                    BasicActions.displaySnackBar(getWindow().getDecorView(),"Acest pacient este deja înscris");
                                }
                            });
                }
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        menu.removeItem(R.id.insert_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_account:
                startActivity(new Intent(this, DoctorDetails.class));
                break;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            Log.d("Logout","Persoana a fost delogată!");
                            FirebaseUtil.attachListener();
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        BasicActions.hideKeyboard(this);
    }

    private void disableControllers(boolean isEnabled){
      addPatient.setEnabled(!isEnabled);
      searchForCNP.setEnabled(!isEnabled);
    }
}

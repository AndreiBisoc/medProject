package com.example.medproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.DoctorToPatientLink;
import com.example.medproject.data.model.Patient;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference firebaseReferenceToPacients, firebaseReferenceToDoctors;
    private List<String> CNPs = new ArrayList<>();
    private AutoCompleteTextView searchForCNP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReferenceToDoctors = mFirebaseDatabase.getReference("Doctors");
        firebaseReferenceToPacients = mFirebaseDatabase.getReference("Patients");
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
        ArrayAdapter < String > adapter = new ArrayAdapter<String>
                        (this, android.R.layout.select_dialog_item, CNPs);

        searchForCNP =  findViewById(R.id.searchForCNP);
        searchForCNP.setThreshold(1); // will start working from first character
        searchForCNP.setAdapter(adapter); // setting the adapter data into the AutoCompleteTextView

        searchForCNP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 12)
                    hideKeyboard();
            }
        });

        final Button addPatient = findViewById(R.id.addPatientButton);
        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String searchedCNP = searchForCNP.getText().toString().trim();
                addPatientToDoctor(searchedCNP);
            }
        });
    }

    public void addPatientToDoctor(final String searchedCNP) {

        final String doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseReferenceToPacients.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                Patient patientToAdd = new Patient(patient.getFirstName(), patient.getLastName(), patient.getBirthDate(), patient.getPhone(),patient.getCNP());
                if(patient.getCNP().equals(searchedCNP)) {
                    String registerDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
                    DoctorToPatientLink link = new DoctorToPatientLink(patientToAdd, registerDate);
                    FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                            .child(doctorUid)
                            .child(patient.getId())
                            .setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(AddPatientToDoctorActivity.this, "Pacientul a fost adăugat cu succes", Toast.LENGTH_LONG).show();
                                finish();
//                                startActivity(new Intent(RegisterPacientActivity.this, Details.class)); - se va crea legatura spre lista de medicatii a noului pacient
                            }
                            // trebuie lucrat la cazurile de eroare pe else
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout","Persoana a fost delogată!");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void hideKeyboard() {
        BasicActions.hideKeyboard(this);
    }
}

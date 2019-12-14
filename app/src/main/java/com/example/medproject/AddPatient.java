package com.example.medproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddPatient extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private List<String> CNPs = new ArrayList<>();
    private AutoCompleteTextView searchForCNP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseReferenceToPacients = mFirebaseDatabase.getReference("Patients");
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

        //Creating the instance of ArrayAdapter containing list of fruit names
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
                String cnp = s.toString();
                if (cnp.length() > 12)
                    hideKeyboard();
            }
        });

        final Button addPatient = findViewById(R.id.addPatientButton);
        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String searchedCNP = searchForCNP.getText().toString();
                addPatientToDoctor(searchedCNP);
            }
        });
    }

    public void addPatientToDoctor(String searchedCNP) {
        FirebaseUser o = mAuth.getCurrentUser();
        String mail = o.getEmail();
        // asta e mailul doctorului ce adauga pacientul cu searchedCNP -- cum obtin lista cu doctori? la fel ca cea cu pacienti? 1000 de linii duplicate ca mai sus?!!
        Toast.makeText(this, searchedCNP, Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

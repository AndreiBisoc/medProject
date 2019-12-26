package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MyPatientsActivity extends AppCompatActivity {

    RecyclerView rvPatients;
    private FirebaseAuth mAuth;
    private final PatientAdapter adapter = new PatientAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        mAuth = FirebaseAuth.getInstance();

        rvPatients = findViewById(R.id.rvPatients);
        rvPatients.setAdapter(adapter);

        LinearLayoutManager patientsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPatients.setLayoutManager(patientsLayoutManager);

        Button addPatientToDoctor = findViewById(R.id.addPatientToDoctorButton);
        addPatientToDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddPatientPage();
            }
        });

    }

    public void goToAddPatientPage(){
        startActivity(new Intent(this, AddPatientToDoctorActivity.class));
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

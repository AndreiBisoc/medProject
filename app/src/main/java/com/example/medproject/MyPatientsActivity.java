package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MyPatientsActivity extends AppCompatActivity {

    private List<Patient> patients;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        RecyclerView rvPatients = findViewById(R.id.rvPatients);
        final PatientAdapter adapter = new PatientAdapter();
        rvPatients.setAdapter(adapter);

        LinearLayoutManager patientsLayoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvPatients.setLayoutManager(patientsLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MyPatientsActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private static RecyclerView rvPatients;
    private static TextView emptyView;
    private static final PatientAdapter adapter = new PatientAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        mAuth = FirebaseAuth.getInstance();

        rvPatients = findViewById(R.id.rvPatients);
        rvPatients.setAdapter(adapter);
        emptyView = findViewById(R.id.empty_view);

        displayMessageOrPatientsList();

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

    public static void displayMessageOrPatientsList() {
        if(adapter.noPatientToDisplay)
        {
            rvPatients.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rvPatients.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
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

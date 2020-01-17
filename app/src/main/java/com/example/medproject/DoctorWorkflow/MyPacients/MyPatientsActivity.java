package com.example.medproject.DoctorWorkflow.MyPacients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
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
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void goToAddPatientPage(){
        startActivity(new Intent(this, AddPatientToDoctorActivity.class));
    }

    public static void displayMessageOrPatientsList() {
        if(adapter.noPatientsToDisplay)
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

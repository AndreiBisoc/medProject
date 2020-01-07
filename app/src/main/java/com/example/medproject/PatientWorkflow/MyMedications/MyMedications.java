package com.example.medproject.PatientWorkflow.MyMedications;

import android.content.Intent;
import android.os.Bundle;

import com.example.medproject.ScanQR;
import com.example.medproject.auth.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class MyMedications extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static RecyclerView rvMedications;
    private static MedicationAdapter secondAdapter;
    private static TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medications);

        String patientId = getIntent().getStringExtra("patientId");

        final MedicationAdapter adapter = new MedicationAdapter(patientId);
        secondAdapter = adapter;

        mAuth = FirebaseAuth.getInstance();

        rvMedications = findViewById(R.id.rvMedications);
        rvMedications.setAdapter(adapter);
        emptyView = findViewById(R.id.empty_view);

        displayMessageOrMedicationsList();

        LinearLayoutManager medicationsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMedications.setLayoutManager(medicationsLayoutManager);

        Button addMedicationButton = findViewById(R.id.addMedicationButton);
        Button scanMedicationButton = findViewById(R.id.scanMedicationButton);
        addMedicationButton.setOnClickListener(this);
        scanMedicationButton.setOnClickListener(this);

        if(!secondAdapter.loggedAsDoctor) {
            addMedicationButton.setVisibility(View.GONE);
            scanMedicationButton.setVisibility(View.VISIBLE);
        } else {
            addMedicationButton.setVisibility(View.VISIBLE);
            scanMedicationButton.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addMedicationButton:
                break;

            case R.id.scanMedicationButton:
                Intent intent = new Intent(view.getContext(), ScanQR.class);
                view.getContext().startActivity(intent);
                break;

            default:

        }
    }

    public static void displayMessageOrMedicationsList() {
        if(secondAdapter.noMedicationsToDisplay)
        {
            rvMedications.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rvMedications.setVisibility(View.VISIBLE);
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

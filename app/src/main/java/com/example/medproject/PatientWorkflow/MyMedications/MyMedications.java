package com.example.medproject.PatientWorkflow.MyMedications;

import android.content.Intent;
import android.os.Bundle;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.DoctorWorkflow.MyPacients.MyPatientsActivity;
import com.example.medproject.DoctorWorkflow.MyPacients.PatientDetails;
import com.example.medproject.DoctorWorkflow.AddMedication.AddMedication;import com.example.medproject.QRCode.ScanQR;
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

import com.example.medproject.R;
import com.example.medproject.auth.RegisterDoctorActivity;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MyMedications extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static RecyclerView rvMedications;
    private static MedicationAdapter secondAdapter;
    private static TextView emptyView;
    private String patientId;
    private boolean loggedAsDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medications);

        loggedAsDoctor = getIntent().getBooleanExtra("loggedAsDoctor", false);

        patientId = getIntent().getStringExtra("patientId");
        String patientName = getIntent().getStringExtra("patientName");

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);

        if(!secondAdapter.loggedAsDoctor) {
            setTitle("Medicațiile mele");
            addMedicationButton.setVisibility(View.GONE);
            scanMedicationButton.setVisibility(View.VISIBLE);
        } else {
            setTitle("Medicațiile lui " + patientName);
            addMedicationButton.setVisibility(View.VISIBLE);
            scanMedicationButton.setVisibility(View.GONE);
        }

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
        if (item.getItemId() == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addMedicationButton:
                Intent intent = new Intent(view.getContext(), AddMedication.class);
                intent.putExtra("patientId", patientId);
                view.getContext().startActivity(intent);
                break;

            case R.id.scanMedicationButton:
                intent = new Intent(view.getContext(), ScanQR.class);
                view.getContext().startActivity(intent);
                break;

            default:

        }
    }

    public static void displayMessageOrMedicationsList() {
        if(!secondAdapter.noMedicationsToDisplay)
        {
            rvMedications.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

        }
        else
        {   rvMedications.setVisibility(View.GONE);
            if(!secondAdapter.loggedAsDoctor) {
                emptyView.setText("Nu aveți nicio medicație adăugată de doctor.");
            }
            emptyView.setVisibility(View.VISIBLE);
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

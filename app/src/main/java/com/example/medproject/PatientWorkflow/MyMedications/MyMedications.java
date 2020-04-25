package com.example.medproject.PatientWorkflow.MyMedications;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.AddMedication.AddMedication;
import com.example.medproject.QRCode.MedicationQRCode.ScanMedicationId;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MyMedications extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static RecyclerView rvMedications;
    private static MedicationAdapter MEDICATION_ADAPTER;
    private static TextView emptyView;
    private static String emptyViewString = "Nu aveți nicio medicație adăugată de doctor.";
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medications);

        boolean loggedAsDoctor = getIntent().getBooleanExtra("loggedAsDoctor", false);
        String doctorId = getIntent().getStringExtra("doctorId");
        String doctorName = getIntent().getStringExtra("doctorName");
        if(doctorName!=null && !Objects.equals(doctorName, "")) {
            emptyViewString = "Momentan doctorul " + doctorName + " nu v-a recomandat nicio medicație.";
        }

        patientId = getIntent().getStringExtra("patientId");
        String patientName = getIntent().getStringExtra("patientName");

        final MedicationAdapter adapter = new MedicationAdapter(patientId, doctorId);
        MEDICATION_ADAPTER = adapter;

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

        if(!MEDICATION_ADAPTER.loggedAsDoctor) {
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
                intent = new Intent(view.getContext(), ScanMedicationId.class);
                view.getContext().startActivity(intent);
                break;

            default:

        }
    }

    public static void displayMessageOrMedicationsList() {
        if(!MEDICATION_ADAPTER.noMedicationsToDisplay)
        {
            rvMedications.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

        }
        else
        {   rvMedications.setVisibility(View.GONE);
            if(!MEDICATION_ADAPTER.loggedAsDoctor) {
                emptyView.setText(emptyViewString);
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

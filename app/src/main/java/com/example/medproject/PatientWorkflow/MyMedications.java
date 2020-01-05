package com.example.medproject.PatientWorkflow;

import android.content.Intent;
import android.os.Bundle;

import com.example.medproject.auth.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;

import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class MyMedications extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private RecyclerView rvMedications;
    private final MedicationAdapter adapter = new MedicationAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medications);

        mAuth = FirebaseAuth.getInstance();

        rvMedications = findViewById(R.id.rvMedications);
        rvMedications.setAdapter(adapter);

        LinearLayoutManager medicationsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMedications.setLayoutManager(medicationsLayoutManager);

        Button button = findViewById(R.id.addMedicationButton);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

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

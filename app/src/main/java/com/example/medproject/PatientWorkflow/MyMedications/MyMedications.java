package com.example.medproject.PatientWorkflow.MyMedications;

import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class MyMedications extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private RecyclerView rvMedications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_medications);

        String patientId = getIntent().getStringExtra("patientId");

        final MedicationAdapter adapter = new MedicationAdapter(patientId);

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

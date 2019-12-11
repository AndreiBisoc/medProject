package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.medproject.data.model.Patient;

import java.util.List;

public class MyPatientsActivity extends AppCompatActivity {

    private RecyclerView rvPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        rvPatients = findViewById(R.id.rvPatients);
        final PatientAdapter adapter = new PatientAdapter();
        rvPatients.setAdapter(adapter);

        LinearLayoutManager patientsLayoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvPatients.setLayoutManager(patientsLayoutManager);

    }

    public void ShowPopup(View v) {

        Intent intent = new Intent(this, DeletePacientPopupActivity.class);
        startActivity(intent);

        // activitate cu theme: dialog in manifest
    }

}

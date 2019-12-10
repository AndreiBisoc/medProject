package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medproject.data.model.Patient;

import java.io.Console;
import java.util.List;

public class MyPatientsActivity extends AppCompatActivity {

    private List<Patient> patients;
    private RecyclerView rvPatients;
    private Button deletebtn, deleteIcon;
    private Dialog deletePatientPopup;
    private TextView closePopup;

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

        deletePatientPopup = new Dialog(this);
    }

    public void ShowPopup(View v) {
        closePopup = findViewById(R.id.closePopup);
        deleteIcon = findViewById(R.id.deleteIcon);
        deletebtn = findViewById(R.id.deletebtn);

        deletePatientPopup.setContentView(R.layout.delete_popup);
        deletePatientPopup.show();

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePatientPopup.dismiss();
            }
        });

//        deletebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.println(Log.VERBOSE, "tag", "alooo");
//            }
//        });

        Toast.makeText(this, "Sterg", Toast.LENGTH_LONG).show();
    }


}

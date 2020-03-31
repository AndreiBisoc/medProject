package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PatientDetails extends AppCompatActivity {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtAddress;
    private boolean canEditForm;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtCNP = findViewById(R.id.txtCNP);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String patientID = intent.getStringExtra("patientID");
        final String loggedUser = FirebaseAuth.getInstance().getUid();

        canEditForm = patientID == null; // logged as doctor or not
        if(canEditForm) {
            setTitle("Contul meu");
        } else {
            setTitle("Detalii pacient");
        }

        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);

            String prenume = txtFirstname.getText().toString().trim();
            String nume = txtLastname.getText().toString().trim();
            String telefon = txtPhone.getText().toString().trim();
            String adresaCabinet = txtAddress.getText().toString().trim();
            String CNP = txtCNP.getText().toString().trim();
            String birthDate = txtBirthDate.getText().toString().trim();
            final Patient patient = new Patient(prenume, nume, birthDate, telefon, adresaCabinet,CNP);
            FirebaseDatabase.getInstance().getReference("Patients")
                    .child(loggedUser)
                    .setValue(patient).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Contul a fost editat cu succes");
                    finish();
                }
            });

            FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshotDoctors: dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshotPatient: snapshotDoctors.getChildren()) {
                                    if(snapshotPatient.getKey().equals(loggedUser)) {
                                        FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                                                .child(snapshotDoctors.getKey())
                                                .child(loggedUser)
                                                .child("patient")
                                                .setValue(patient);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        });

        DatabaseReference mDatabaseReference;
        if(canEditForm) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + loggedUser);
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + patientID);
            saveChangesButton.setVisibility(View.GONE);
        }

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                txtLastname.setText(patient.getLastName());
                txtFirstname.setText(patient.getFirstName());
                txtCNP.setText(patient.getCNP());
                txtBirthDate.setText(patient.getBirthDate());
                txtPhone.setText(patient.getPhone());
                txtAddress.setText(patient.getAddress());
                disableControllers(!canEditForm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
                break;
        }
        return true;
    }

    private void disableControllers(boolean isEnabled){
        txtLastname.setEnabled(!isEnabled);
        txtFirstname.setEnabled(!isEnabled);
        txtCNP.setEnabled(!isEnabled);
        txtBirthDate.setEnabled(!isEnabled);
        txtPhone.setEnabled(!isEnabled);
        txtAddress.setEnabled(!isEnabled);
    }
}

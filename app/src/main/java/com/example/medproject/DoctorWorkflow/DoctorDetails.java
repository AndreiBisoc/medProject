package com.example.medproject.DoctorWorkflow;

import android.content.Intent;
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
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DoctorDetails extends AppCompatActivity {

    private EditText txtLastname, txtFirstname, txtSpecializare, txtPhone, txtAddress;
    private Button saveChangesButton;
    private ProgressBar progressBar;
    private String doctorName;
    private boolean canEditForm;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.doctor_details);
        setTitle("Contul meu");

        final String loggedUser = FirebaseAuth.getInstance().getUid();

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        Intent intent = getIntent();
        String doctorID = intent.getStringExtra("doctorID");

        canEditForm = doctorID == null; // logged as doctor or not
        if(canEditForm) {
            setTitle("Contul meu");
        } else {
            setTitle("Detalii doctor");
        }

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtSpecializare = findViewById(R.id.txtSpecializare);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        progressBar = findViewById(R.id.progressBar);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);

            final String prenume = txtFirstname.getText().toString().trim();
            final String nume = txtLastname.getText().toString().trim();
            String telefon = txtPhone.getText().toString().trim();
            String adresaCabinet = txtAddress.getText().toString().trim();
            String specialization = txtSpecializare.getText().toString().trim();
            Doctor doctor = new Doctor(prenume, nume, specialization, telefon, adresaCabinet);
            FirebaseDatabase.getInstance().getReference("Doctors")
                    .child(loggedUser)
                    .setValue(doctor).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Contul a fost editat cu succes");
                    finish();
                }
            });

            FirebaseDatabase.getInstance().getReference("Medications")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshotMedication: dataSnapshot.getChildren()) {
                                if(snapshotMedication.child("doctorName").getValue().equals(doctorName) ) {
                                    FirebaseDatabase.getInstance().getReference("Medications")
                                            .child(snapshotMedication.getKey())
                                            .child("doctorName")
                                            .setValue(nume + " " + prenume);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference("PatientToMedications")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshotPatient: dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshotMedication: snapshotPatient.getChildren()) {
                                    if(snapshotMedication.child("doctorName").getValue().equals(doctorName)) {
                                        FirebaseDatabase.getInstance().getReference("PatientToMedications")
                                                .child(snapshotPatient.getKey())
                                                .child(snapshotMedication.getKey())
                                                .child("doctorName")
                                                .setValue(nume + " " + prenume);
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
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors/" + loggedUser);
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors/" + doctorID);
            saveChangesButton.setVisibility(View.GONE);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                txtLastname.setText(doctor.getLastName());
                txtFirstname.setText(doctor.getFirstName());
                txtSpecializare.setText(doctor.getSpecialization());
                txtPhone.setText(doctor.getPhone());
                txtAddress.setText(doctor.getAdresaCabinet());
                doctorName = txtLastname.getText() + " " + txtFirstname.getText();
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
        menu.removeItem(R.id.edit_account);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
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
        txtAddress.setEnabled(!isEnabled);
        txtPhone.setEnabled(!isEnabled);
        txtSpecializare.setEnabled(!isEnabled);
        saveChangesButton.setEnabled(!isEnabled);
    }
}

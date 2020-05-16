package com.example.medproject.DoctorWorkflow;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.data.Models.Patient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class PatientDetails extends AppCompatActivity implements View.OnClickListener {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtAddress;
    private ImageView userIcon;
    private boolean loggedAsDoctor;
    private ProgressBar progressBar;
    private String loggedUser;
    private String patientID;
    private String patientName;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.patient_details);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        Intent intent = getIntent();
        patientID = intent.getStringExtra("patientID");
        patientName = intent.getStringExtra("patientName");

        loggedAsDoctor = intent.getBooleanExtra("loggedAsDoctor", false);
        if (!loggedAsDoctor) {
            setTitle("Contul meu");
        } else {
            setTitle("Detalii pacient");
        }

        loggedUser = FirebaseAuth.getInstance().getUid();

        userIcon = findViewById(R.id.patientIcon);
        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtCNP = findViewById(R.id.txtCNP);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        progressBar = findViewById(R.id.progressBar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);

        Button saveChangesOrDeletePatientButton = findViewById(R.id.saveChangesOrDeletePatientButton);
        saveChangesOrDeletePatientButton.setOnClickListener(this);

        DatabaseReference mDatabaseReference;
        if (!loggedAsDoctor) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + loggedUser);
            saveChangesOrDeletePatientButton.setBackgroundColor(getColor(R.color.amber));
            saveChangesOrDeletePatientButton.setText(getText(R.string.save_changes));
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + patientID);
            saveChangesOrDeletePatientButton.setBackgroundColor(getColor(R.color.red));
            saveChangesOrDeletePatientButton.setText(getText(R.string.delete_patient));
        }

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                String imageUrl = ResourcesHelper.ICONS.get("defaultPatientIconURL");
                if(patient.getImage() != null) {
                    imageUrl = patient.getImage().getImageUrl();
                }
                Picasso.get()
                        .load(imageUrl)
                        .into(userIcon);
                txtLastname.setText(patient.getLastName());
                txtFirstname.setText(patient.getFirstName());
                txtCNP.setText(patient.getCNP());
                txtBirthDate.setText(patient.getBirthDate());
                txtPhone.setText(patient.getPhone());
                txtAddress.setText(patient.getAddress());
                disableControllers(loggedAsDoctor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (loggedAsDoctor) {
            deletePatient();
        } else {
            // never used
            saveChanges();
        }
    }

    private void deletePatient() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ștergere " + patientName)
                .setMessage("Sunteți sigur că doriți să ștergeți acest pacient?")
                .setNegativeButton("Anulare", /* listener = */ null)
                .setPositiveButton("Ștergere", (dialog, which) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    databaseReference.child("DoctorsToPatients")
                            .child(doctorUid)
                            .child(patientID)
                            .removeValue();

                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Pacientul " + patientName + " a fost șters cu succes");
                    Intent toMyPatients = new Intent(this, MyPatientsOrMyDoctorsActivity.class);
                    toMyPatients.putExtra("loggedAsDoctor", true);
                    this.startActivity(toMyPatients);
                })
                .show();
    }

    private void saveChanges() {
        // never used - see PatientDetails_Fragment.saveChanges()
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        String firstName = txtFirstname.getText().toString().trim();
        String lastName = txtLastname.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String CNP = txtCNP.getText().toString().trim();
        String birthDate = txtBirthDate.getText().toString().trim();
        Patient patient = new Patient();
        Map<String, Object> detailsToUpdate = patient.setBasicDetails(firstName, lastName, phone, address, CNP, birthDate);
        FirebaseDatabase.getInstance().getReference("Patients")
                .child(loggedUser)
                .updateChildren(detailsToUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                BasicActions.displaySnackBar(getWindow().getDecorView(), "Contul a fost editat cu succes");
                progressBar.setVisibility(View.GONE);
                disableControllers(false);
            }
        });

        FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshotDoctors : dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshotPatient : snapshotDoctors.getChildren()) {
                                if (snapshotPatient.getKey().equals(loggedUser)) {
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

    private void disableControllers(boolean isEnabled) {
        txtLastname.setEnabled(!isEnabled);
        txtFirstname.setEnabled(!isEnabled);
        txtCNP.setEnabled(!isEnabled);
        txtBirthDate.setEnabled(!isEnabled);
        txtPhone.setEnabled(!isEnabled);
        txtAddress.setEnabled(!isEnabled);
    }
}

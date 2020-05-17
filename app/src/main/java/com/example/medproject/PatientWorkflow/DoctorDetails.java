package com.example.medproject.PatientWorkflow;

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

import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.R;
import com.example.medproject.Models.Doctor;
import com.example.medproject.Models.UploadedImage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorDetails extends AppCompatActivity implements View.OnClickListener {

    private EditText txtLastname, txtFirstname, txtSpecializare, txtPhone, txtAddress;
    private Button saveChangesOrDeletePatientButton;
    private ProgressBar progressBar;
    private boolean loggedAsDoctor;
    private String loggedUser;
    private String doctorId;
    private String doctorName;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.doctor_details);

        loggedUser = FirebaseAuth.getInstance().getUid();

        Intent intent = getIntent();
        doctorId = intent.getStringExtra("doctorID");
        doctorName = intent.getStringExtra("doctorName");
        loggedAsDoctor = intent.getBooleanExtra("loggedAsDoctor", false);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        if(loggedAsDoctor) {
            setTitle("Contul meu");
        } else {
            setTitle("Detalii doctor");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtSpecializare = findViewById(R.id.txtSpecializare);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        progressBar = findViewById(R.id.progressBar);

        saveChangesOrDeletePatientButton = findViewById(R.id.saveChangesOrDeletePatientButton);
        saveChangesOrDeletePatientButton.setOnClickListener(this);

        DatabaseReference mDatabaseReference;
        if(loggedAsDoctor) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors/" + loggedUser);
            saveChangesOrDeletePatientButton.setBackgroundColor(getColor(R.color.amber));
            saveChangesOrDeletePatientButton.setText(getText(R.string.save_changes));
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors/" + doctorId);
            saveChangesOrDeletePatientButton.setBackgroundColor(getColor(R.color.red));
            saveChangesOrDeletePatientButton.setText(getText(R.string.delete_doctor));
        }

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                CircleImageView doctorIcon = findViewById(R.id.doctorIcon);
                UploadedImage uploadedImage = doctor.getImage();
                if (uploadedImage != null) {
                    Picasso.get().load(uploadedImage.getImageUrl()).into(doctorIcon);
                } else {
                    Picasso.get().load(ResourcesHelper.ICONS.get("defaultUserIconURL")).into(doctorIcon);
                }
                txtLastname.setText(doctor.getLastName());
                txtFirstname.setText(doctor.getFirstName());
                txtSpecializare.setText(doctor.getSpecialization());
                txtPhone.setText(doctor.getPhone());
                txtAddress.setText(doctor.getAdresaCabinet());
                doctorName = txtLastname.getText() + " " + txtFirstname.getText();
                disableControllers(!loggedAsDoctor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (loggedAsDoctor) {
            // this if-branch is never used since the DoctorDetails_Fragment activity is used
            saveChanges();
        } else {
            deleteDoctor();
        }
    }

    private void deleteDoctor() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ștergere dr. " + doctorName)
                .setMessage("Sunteți sigur că doriți să nu mai fiți pacientul acestui doctor?")
                .setNegativeButton("Înapoi", /* listener = */ null)
                .setPositiveButton("Ștergere", (dialog, which) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    databaseReference.child("DoctorsToPatients")
                            .child(doctorId)
                            .child(patientId)
                            .removeValue();

                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Doctorul " + doctorName + " a fost șters cu succes");
                    Intent toMyDoctors = new Intent(this, MyPatientsOrMyDoctorsActivity.class);
                    toMyDoctors.putExtra("loggedAsDoctor", false);
                    this.startActivity(toMyDoctors);
                }).show();
    }

    private void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);

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
                progressBar.setVisibility(View.GONE);
                disableControllers(true);
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

    private void disableControllers(boolean shouldBeDisabled){
        txtLastname.setEnabled(!shouldBeDisabled);
        txtFirstname.setEnabled(!shouldBeDisabled);
        txtAddress.setEnabled(!shouldBeDisabled);
        txtPhone.setEnabled(!shouldBeDisabled);
        txtSpecializare.setEnabled(!shouldBeDisabled);
        if(shouldBeDisabled) {
            int disabledTextColorId = getColor(R.color.grey);
            txtLastname.setTextColor(disabledTextColorId);
            txtFirstname.setTextColor(disabledTextColorId);
            txtAddress.setTextColor(disabledTextColorId);
            txtPhone.setTextColor(disabledTextColorId);
            txtSpecializare.setTextColor(disabledTextColorId);
        }
    }
}

package com.example.medproject.DoctorWorkflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.MyPacients.AddPatientToDoctorActivity;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DoctorDetails extends AppCompatActivity {

    private EditText txtLastname, txtFirstname, txtSpecializare, txtPhone, txtAddress;
    private Button saveChangesButton;
    private DatabaseReference mDatabaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_details);

        final String doctorId = FirebaseAuth.getInstance().getUid();
        mDatabaseReference  = FirebaseDatabase.getInstance().getReference("Doctors/" + doctorId);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtSpecializare = findViewById(R.id.txtSpecializare);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        progressBar = findViewById(R.id.progressBar);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                disableControllers(true);

                String prenume = txtFirstname.getText().toString().trim();
                String nume = txtLastname.getText().toString().trim();
                String telefon = txtPhone.getText().toString().trim();
                String adresaCabinet = txtAddress.getText().toString().trim();
                String specialization = txtSpecializare.getText().toString().trim();
                Doctor doctor = new Doctor(prenume, nume, specialization, telefon, adresaCabinet);
                FirebaseDatabase.getInstance().getReference("Doctors")
                        .child(doctorId)
                        .setValue(doctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DoctorDetails.this, "Contul a fost editat cu succes", Toast.LENGTH_LONG).show();
                                finish();
                            }
                    }
                });
            }
        });

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                txtLastname.setText(doctor.getLastName());
                txtFirstname.setText(doctor.getFirstName());
                txtSpecializare.setText(doctor.getSpecialization());
                txtPhone.setText(doctor.getPhone());
                txtAddress.setText(doctor.getAdresaCabinet());
                disableControllers(false);
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
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
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

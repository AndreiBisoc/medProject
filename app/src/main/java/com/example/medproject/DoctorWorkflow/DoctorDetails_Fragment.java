package com.example.medproject.DoctorWorkflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.medproject.BasicActions;
import com.example.medproject.R;
import com.example.medproject.data.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorDetails_Fragment extends Fragment {
    private EditText txtLastname, txtFirstname, txtSpecializare, txtPhone, txtAddress;
    private Button saveChangesButton;
    private ProgressBar progressBar;
    private String doctorName;

    public DoctorDetails_Fragment() {// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final String loggedUser = FirebaseAuth.getInstance().getUid();
        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(getView().findViewById(R.id.container), (AppCompatActivity) getActivity());

        txtLastname = getView().findViewById(R.id.txtLastname);
        txtFirstname = getView().findViewById(R.id.txtFirstName);
        txtSpecializare = getView().findViewById(R.id.txtSpecializare);
        txtPhone = getView().findViewById(R.id.txtPhone);
        txtAddress = getView().findViewById(R.id.txtAddress);
        progressBar = getView().findViewById(R.id.progressBar);

        saveChangesButton = getView().findViewById(R.id.saveChangesButton);
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
                    View view = getActivity().getWindow().getDecorView();
                    BasicActions.displaySnackBar(view, "Contul a fost editat cu succes");
                    progressBar.setVisibility(View.GONE);
                    disableControllers(false);
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

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors/" + loggedUser);

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
                disableControllers(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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

package com.example.medproject.MyAccount;

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
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Models.Doctor;
import com.example.medproject.Models.UploadedImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorDetails_Fragment extends Fragment {
    private EditText txtLastname, txtFirstname, txtSpecializare, txtPhone, txtAddress;
    private Button saveChangesOrDeletePatientButton;
    private ProgressBar progressBar;
    private String doctorName;
    private CircleImageView doctorIcon;

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
        BasicActions.hideKeyboardWithClick(requireView().findViewById(R.id.container), (AppCompatActivity) getActivity());

        View view = getView();
        txtLastname = view.findViewById(R.id.txtLastname);
        txtFirstname = view.findViewById(R.id.txtFirstName);
        txtSpecializare = view.findViewById(R.id.txtSpecializare);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        progressBar = view.findViewById(R.id.progressBar);
        doctorIcon = view.findViewById(R.id.doctorIcon);

        saveChangesOrDeletePatientButton = getView().findViewById(R.id.saveChangesOrDeletePatientButton);
        saveChangesOrDeletePatientButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);

            final String firstName = txtFirstname.getText().toString().trim();
            final String lastName = txtLastname.getText().toString().trim();
            String phone = txtPhone.getText().toString().trim();
            String adresaCabinet = txtAddress.getText().toString().trim();
            String specialization = txtSpecializare.getText().toString().trim();
            Doctor doctor = new Doctor();
            Map<String, Object> detailsToUpdate = doctor.updateDoctorDetails(firstName, lastName, phone, adresaCabinet, specialization);
            assert loggedUser != null;
            FirebaseDatabase.getInstance().getReference("Doctors")
                    .child(loggedUser)
                    .updateChildren(detailsToUpdate).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    View myView = getActivity().getWindow().getDecorView();
                    BasicActions.displaySnackBar(myView, "Contul a fost editat cu succes");
                    progressBar.setVisibility(View.GONE);
                    disableControllers(false);
                }
            });

            FirebaseDatabase.getInstance().getReference("Medications")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshotMedication: dataSnapshot.getChildren()) {
                                if(Objects.equals(snapshotMedication.child("doctorName").getValue(), doctorName)) {
                                    FirebaseDatabase.getInstance().getReference("Medications")
                                            .child(Objects.requireNonNull(snapshotMedication.getKey()))
                                            .child("doctorName")
                                            .setValue(firstName + " " + lastName);
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
                                    if(Objects.equals(snapshotMedication.child("doctorName").getValue(), doctorName)) {
                                        FirebaseDatabase.getInstance().getReference("PatientToMedications")
                                                .child(Objects.requireNonNull(snapshotPatient.getKey()))
                                                .child(Objects.requireNonNull(snapshotMedication.getKey()))
                                                .child("doctorName")
                                                .setValue(firstName + " " + lastName);
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
                assert doctor != null;
                txtLastname.setText(doctor.getLastName());
                txtFirstname.setText(doctor.getFirstName());
                txtSpecializare.setText(doctor.getSpecialization());
                txtPhone.setText(doctor.getPhone());
                txtAddress.setText(doctor.getAdresaCabinet());
                doctorName = txtLastname.getText() + " " + txtFirstname.getText();
                UploadedImage uploadedImage = doctor.getImage();
                if (uploadedImage != null) {
                    Picasso.get()
                            .load(uploadedImage.getImageUrl())
                            .into(doctorIcon);
                } else {
                    Picasso.get().
                            load(ResourcesHelper.ICONS.get("defaultUserIconURL")).into(doctorIcon);
                }
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
        saveChangesOrDeletePatientButton.setEnabled(!isEnabled);
    }
}

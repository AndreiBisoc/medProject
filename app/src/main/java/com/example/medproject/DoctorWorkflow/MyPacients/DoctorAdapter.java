package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.data.model.Doctor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public boolean noDoctorsToDisplay = true;
    private final ArrayList<Doctor> doctors;

    public DoctorAdapter(boolean loggedAsDoctor){

        String loggedPatientUid = FirebaseAuth.getInstance().getUid();

        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("DoctorsToPatients", l);
        DatabaseReference mDatabaseReference = FirebaseUtil.mDatabaseReference;
        doctors = FirebaseUtil.mDoctors;
        ChildEventListener loadDoctors = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    if(ds.getKey().equals(loggedPatientUid)) {
                        String doctorId = dataSnapshot.getKey();
                        if(noDoctorsToDisplay) {
                            noDoctorsToDisplay = false;
                            MyPatientsActivity.displayMessageOrList(false);
                        }
                        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId);
                        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                doctors.add(doctor);
                                notifyItemInserted(doctors.size() - 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                not implemented to show changes to a doctor account in real time
                String removedDoctorId = dataSnapshot.getKey();
                int position = 0;
                for(Doctor doctor : doctors) {
                    if(Objects.equals(doctor.getId(), removedDoctorId)) {
                        doctors.remove(doctor);
                        break;
                    }
                    position++;
                }
                notifyItemRemoved(position);
                if (doctors.size() == 0) {
                    noDoctorsToDisplay = true;
                    MyPatientsActivity.displayMessageOrList(false);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//              The code here is the same as above because when deleting a doctor-patient link, if the patient was the only patient of the doctor
//              the entire node is deleted, not only changed
                String removedDoctorId = dataSnapshot.getKey();
                int position = 0;
                for(Doctor doctor : doctors) {
                    if(Objects.equals(doctor.getId(), removedDoctorId)) {
                        doctors.remove(doctor);
                        break;
                    }
                    position++;
                }
                notifyItemRemoved(position);
                if (doctors.size() == 0) {
                    noDoctorsToDisplay = true;
                    MyPatientsActivity.displayMessageOrList(false);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(loadDoctors);

    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.doctor, parent, false);

        return new DoctorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.bind(doctor);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView name;
        private final TextView specialization;
        private final TextView phoneNumber;

        DoctorViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.doctorName);
            specialization = itemView.findViewById(R.id.doctorSpecialization);
            phoneNumber = itemView.findViewById(R.id.doctorPhoneNumber);

            Button deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);

            Button seeDoctorDetails = itemView.findViewById(R.id.seeDoctorDetails);
            seeDoctorDetails.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void bind(Doctor doctor){
            name.setText(doctor.getName());
            specialization.setText(doctor.getSpecialization());
            phoneNumber.setText(doctor.getPhone());
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            final Doctor selectedDoctor = doctors.get(position);
            final View context = view;
            switch (view.getId()) {
                case R.id.deleteIcon:
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Ștergere dr. " + selectedDoctor.getName())
                            .setMessage("Sunteți sigur că doriți să nu mai fiți pacientul acestui doctor?")
                            .setNegativeButton("Înapoi", /* listener = */ null)
                            .setPositiveButton("Ștergere", (dialog, which) -> {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                databaseReference.child("DoctorsToPatients")
                                        .child(selectedDoctor.getId())
                                        .child(patientId)
                                        .removeValue();

                                BasicActions.displaySnackBar(context, "Doctorul " + selectedDoctor.getName() + " a fost șters cu succes.");
                            }).show();
                    break;

                case R.id.seeDoctorDetails:
                    Intent intent = new Intent(view.getContext(), DoctorDetails.class);
                    intent.putExtra("doctorID", selectedDoctor.getId());
                    intent.putExtra("loggedAsDoctor", false);
                    view.getContext().startActivity(intent);
                    break;

                default:
                    Intent displayMedicationsEnteredByThisDoctor = new Intent(view.getContext(), MyMedications.class);
                    displayMedicationsEnteredByThisDoctor.putExtra("doctorId", selectedDoctor.getId());
                    displayMedicationsEnteredByThisDoctor.putExtra("doctorName", selectedDoctor.getName());
                    displayMedicationsEnteredByThisDoctor.putExtra("loggedAsDoctor", false);
                    view.getContext().startActivity(displayMedicationsEnteredByThisDoctor);
                    break;
            }
        }

    }
}

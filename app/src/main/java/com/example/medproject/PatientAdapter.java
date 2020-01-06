package com.example.medproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.data.model.DoctorToPatientLink;
import com.example.medproject.data.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public boolean noPatientToDisplay = true;
    private ArrayList<Patient> patients;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public PatientAdapter(){

        String loggedDoctorUid = FirebaseAuth.getInstance().getUid();

        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("DoctorsToPatients", l);
        mDatabaseReference = FirebaseUtil.mDatabaseReference.child(loggedDoctorUid);
        patients = FirebaseUtil.mPatients;
        mChildListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                DoctorToPatientLink doctorToPatientLink = dataSnapshot.getValue(DoctorToPatientLink.class);
                if(doctorToPatientLink.getPatient() != null) {
                    Patient patient = doctorToPatientLink.getPatient();
                    patient.setId(dataSnapshot.getKey());
                    patients.add(patient);
                    notifyItemInserted(patients.size() - 1);
                    noPatientToDisplay = false;
                    MyPatientsActivity.displayMessageOrPatientsList();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DoctorToPatientLink doctorToPatientLink = dataSnapshot.getValue(DoctorToPatientLink.class);
                Patient patient = doctorToPatientLink.getPatient();
                int position = patients.indexOf(patient);
                patients.remove(patient);
                notifyItemRemoved(position);
                if(patients.size() == 0) {
                    noPatientToDisplay = true;
                    MyPatientsActivity.displayMessageOrPatientsList();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.patient, parent, false);

        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name, dateOfBirth, phoneNumber;
        private Button deleteIcon, seeMedication;

        public PatientViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.patientName);
            dateOfBirth = itemView.findViewById(R.id.patientDateOfBirth);
            phoneNumber = itemView.findViewById(R.id.patientPhoneNumber);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);

            seeMedication = itemView.findViewById(R.id.seeMoreIcon);
            seeMedication.setOnClickListener(this);
        }

        public void bind(Patient patient){
            name.setText(patient.getName());
            dateOfBirth.setText(patient.getBirthDate());
            phoneNumber.setText(patient.getPhone());
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Patient selectedPatient = patients.get(position);

            switch (view.getId()) {
                case R.id.deleteIcon:
                    Intent intent = new Intent(view.getContext(), DeletePacientPopupActivity.class);
                    intent.putExtra("Patient", selectedPatient);
                    view.getContext().startActivity(intent);
                    break;

                default:
                    Toast.makeText(view.getContext(), "Ai clickuit aici", Toast.LENGTH_LONG).show();
                    intent = new Intent(view.getContext(), MyMedications.class);
                    intent.putExtra("patientId", selectedPatient.getId());
                    view.getContext().startActivity(intent);

            }
        }

    }
}

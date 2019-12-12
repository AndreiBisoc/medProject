package com.example.medproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.data.model.Patient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    ArrayList<Patient> patients;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public PatientAdapter(){
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("Patients", l);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        patients = FirebaseUtil.mPatients;
        mChildListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                patient.setId(dataSnapshot.getKey());
                patients.add(patient);
                notifyItemInserted(patients.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

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
        private Button deleteIcon;

        public PatientViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.patientName);
            dateOfBirth = itemView.findViewById(R.id.patientDateOfBirth);
            phoneNumber = itemView.findViewById(R.id.patientPhoneNumber);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);
        }

        public void bind(Patient patient){
            name.setText(patient.getName());
//            dateOfBirth.setText(patient.getBirthDate().toString());
            dateOfBirth.setText("25 Jan 1998");
            phoneNumber.setText(patient.getPhone());
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            Patient selectedPatient = patients.get(position);
            Intent intent = new Intent(view.getContext(), DeletePacientPopupActivity.class);
            intent.putExtra("Patient", selectedPatient);
            view.getContext().startActivity(intent);
        }
    }
}

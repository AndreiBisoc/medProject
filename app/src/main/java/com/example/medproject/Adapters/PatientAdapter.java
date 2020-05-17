package com.example.medproject.Adapters;

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

import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.DoctorWorkflow.PatientDetails;
import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Models.DoctorToPatientLink;
import com.example.medproject.Models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public boolean noPatientsToDisplay = true;
    private final ArrayList<Patient> patients;
    public final ArrayList<String> patientsCNPs = new ArrayList<>();

    public PatientAdapter() {

        String loggedDoctorUid = FirebaseAuth.getInstance().getUid();
        if(loggedDoctorUid == null)
            loggedDoctorUid = "";

        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("DoctorsToPatients", l);
        DatabaseReference mDatabaseReference = FirebaseUtil.mDatabaseReference.child(loggedDoctorUid);
        patients = new ArrayList<>();
        ChildEventListener mChildListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DoctorToPatientLink doctorToPatientLink = dataSnapshot.getValue(DoctorToPatientLink.class);
                if (doctorToPatientLink.getPatient() != null) {
                    Patient patient = doctorToPatientLink.getPatient();
                    patient.setId(dataSnapshot.getKey());
                    patients.add(patient);
                    patientsCNPs.add(patient.getCNP());
                    notifyItemInserted(patients.size() - 1);
                    if (noPatientsToDisplay) {
                        noPatientsToDisplay = false;
                        MyPatientsOrMyDoctorsActivity.displayMessageOrList(true);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DoctorToPatientLink doctorToPatientLink = dataSnapshot.getValue(DoctorToPatientLink.class);
                Patient patient = doctorToPatientLink.getPatient();
                int position = patientsCNPs.indexOf(patient.getCNP());
                patients.set(position, patient);
                notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DoctorToPatientLink doctorToPatientLink = dataSnapshot.getValue(DoctorToPatientLink.class);
                Patient patient = doctorToPatientLink.getPatient();
                int position = patients.indexOf(patient);
                patients.remove(patient);
                patientsCNPs.remove(patient.getCNP());
                notifyItemRemoved(position);
                if (patients.size() == 0) {
                    noPatientsToDisplay = true;
                    MyPatientsOrMyDoctorsActivity.displayMessageOrList(true);
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
                .inflate(R.layout.card_view, parent, false);

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

    public class PatientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView phoneNumber;
        private CircleImageView userIcon;

        PatientViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cardView_title);
            phoneNumber = itemView.findViewById(R.id.cardView_subtitle);
            userIcon = itemView.findViewById(R.id.cardView_icon);

            Button seeMedications = itemView.findViewById(R.id.cardView_button);
            seeMedications.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void bind(Patient patient) {
            name.setText(patient.getName());
            phoneNumber.setText(patient.getPhone());
            String imageUrl = ResourcesHelper.ICONS.get("defaultPatientIconURL");
            if(patient.getImage() != null) {
                imageUrl = patient.getImage().getImageUrl();
            }
            Picasso.get()
                    .load(imageUrl)
                    .into(userIcon);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            final Patient selectedPatient = patients.get(position);

            if (view.getId() == R.id.cardView_button) {
                Intent intent = new Intent(view.getContext(), MyMedications.class);
                intent.putExtra("patientName", selectedPatient.getName());
                intent.putExtra("patientId", selectedPatient.getId());
                intent.putExtra("loggedAsDoctor", true);
                view.getContext().startActivity(intent);
            } else {
                Intent intent;
                intent = new Intent(view.getContext(), PatientDetails.class);
                intent.putExtra("patientID", selectedPatient.getId());
                intent.putExtra("patientName", selectedPatient.getName());
                intent.putExtra("loggedAsDoctor", true);
                view.getContext().startActivity(intent);
            }
        }
    }
}

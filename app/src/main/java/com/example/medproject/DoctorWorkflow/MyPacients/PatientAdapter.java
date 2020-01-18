package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.BasicActions;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.data.model.DoctorToPatientLink;
import com.example.medproject.data.model.Patient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public boolean noPatientsToDisplay = true;
    private ArrayList<Patient> patients;
    private ArrayList<String> patientsCNPs = new ArrayList<>();
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
                    patientsCNPs.add(patient.getCNP());
                    notifyItemInserted(patients.size() - 1);
                    noPatientsToDisplay = false;
                    MyPatientsActivity.displayMessageOrPatientsList();
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
                if(patients.size() == 0) {
                    noPatientsToDisplay = true;
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
        private Button deleteIcon, seeMedications;

        public PatientViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.patientName);
            dateOfBirth = itemView.findViewById(R.id.patientDateOfBirth);
            phoneNumber = itemView.findViewById(R.id.patientPhoneNumber);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);

            seeMedications = itemView.findViewById(R.id.seeMore);
            seeMedications.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Patient patient){
            name.setText(patient.getName());
            dateOfBirth.setText(patient.getBirthDate());
            phoneNumber.setText(patient.getPhone());
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            final Patient selectedPatient = patients.get(position);
            final View context = view;
            switch (view.getId()) {
                case R.id.deleteIcon:
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Ștergere " + selectedPatient.getName())
                            .setMessage("Sunteți sigur că doriți să ștergeți acest pacient?")
                            .setNegativeButton("Anulare", /* listener = */ null)
                            .setPositiveButton("Ștergere", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    String doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    databaseReference.child("DoctorsToPatients")
                                            .child(doctorUid)
                                            .child(selectedPatient.getId())
                                            .removeValue();

                                    BasicActions.displaySnackBar(context, "Pacientul " + selectedPatient.getName() + " a fost șters cu succes");
                                }
                            })
                            .show();
                    break;

                case R.id.seeMore:
                    Intent intent = new Intent(view.getContext(), PatientDetails.class);
                    intent.putExtra("patientID", selectedPatient.getId());
                    view.getContext().startActivity(intent);

                    break;

                default:
                    intent = new Intent(view.getContext(), MyMedications.class);
                    intent.putExtra("patientName", selectedPatient.getName());
                    intent.putExtra("patientId", selectedPatient.getId());
                    view.getContext().startActivity(intent);
                    break;
            }
        }

    }
}

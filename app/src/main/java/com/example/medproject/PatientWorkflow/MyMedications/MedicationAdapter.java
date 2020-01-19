package com.example.medproject.PatientWorkflow.MyMedications;

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
import com.example.medproject.PatientWorkflow.MyDrugs.MyDrugs;
import com.example.medproject.R;
import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.Medication;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    public boolean noMedicationsToDisplay = true;
    public boolean loggedAsDoctor;
    private String currentUser;
    private ArrayList<Medication> medications;
    private DatabaseReference mDatabaseReference;
    String patientIdCopy;

    public MedicationAdapter(String patientId) {
        patientIdCopy = patientId;
        loggedAsDoctor = patientId != null;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String idToSearchMedication = loggedAsDoctor ? patientId : currentUser;
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("PatientToMedications/" + idToSearchMedication, l);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        medications = FirebaseUtil.mMedications;
        mDatabaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Medication medication = dataSnapshot.getValue(Medication.class);
                medication.setId(dataSnapshot.getKey());
                if(medication != null) {
                    noMedicationsToDisplay = false;
                    MyMedications.displayMessageOrMedicationsList();
                }
                medications.add(medication);
                notifyItemInserted(medications.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Medication medication = dataSnapshot.getValue(Medication.class);
                medication.setId(dataSnapshot.getKey());
                int position = medications.indexOf(medication);
                medications.set(position, medication);
                notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Medication medication = dataSnapshot.getValue(Medication.class);
                medication.setId(dataSnapshot.getKey());
                int position = medications.indexOf(medication);
                medications.remove(medication);
                notifyItemRemoved(position);
                if(medications.size() == 0) {
                    noMedicationsToDisplay = true;
                    MyMedications.displayMessageOrMedicationsList();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.medication, parent, false);

        return new MedicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medications.get(position);
        holder.bind(medication);
        if(!loggedAsDoctor)
            holder.deleteIcon.setVisibility(GONE);
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }


    public class MedicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView diagnostic, numeDoctor;
        private Button deleteIcon;
        private boolean canEditMedicationFlag = false;

        public void canEdit(final String numeDoctor) {
            DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference("Doctors");

            doctorsRef.child(currentUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    String name = doctor.getName();
                    if(name.equals(numeDoctor)) {
                        deleteIcon.setVisibility(View.VISIBLE);
                        canEditMedicationFlag = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public MedicationViewHolder(View itemView) {
            super(itemView);
            diagnostic = itemView.findViewById(R.id.medicationDiagnostic);
            numeDoctor = itemView.findViewById(R.id.doctorName);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Medication medication) {
            deleteIcon.setVisibility(View.INVISIBLE);
            diagnostic.setText(medication.getDiagnostic());
            numeDoctor.setText(medication.getDoctorName());
            if(loggedAsDoctor) {
                canEdit(medication.getDoctorName());
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            final Medication selectedMedication = medications.get(position);
            final View context = view;

            switch (view.getId()) {
                case R.id.deleteIcon:
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Ștergere " + selectedMedication.getDiagnostic())
                            .setMessage("Sunteți sigur că doriți să ștergeți această medicație?")
                            .setNegativeButton("Anulare", /* listener = */ null)
                            .setPositiveButton("Ștergere", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("PatientToMedications")
                                            .child(patientIdCopy)
                                            .child(selectedMedication.getId())
                                            .removeValue();

                                    databaseReference.child("Medications")
                                            .child(selectedMedication.getId())
                                            .removeValue();

                                    BasicActions.displaySnackBar(context, "Medicația " + selectedMedication.getDiagnostic() + " a fost ștearsă cu succes");
                                }
                            }).show();
                    break;

                default:
                    String medicationID = selectedMedication.getId();
                    Intent intent = new Intent(view.getContext(), MyDrugs.class);
                    intent.putExtra("diagnostic", selectedMedication.getDiagnostic());
                    intent.putExtra("MedicationID", medicationID);
                    intent.putExtra("canEditMedicationFlag", canEditMedicationFlag);
                    view.getContext().startActivity(intent);
                    break;
            }
        }
    }
}

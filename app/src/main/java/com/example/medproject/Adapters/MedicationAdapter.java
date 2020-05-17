package com.example.medproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.PatientWorkflow.MyDrugs;
import com.example.medproject.PatientWorkflow.MyMedications;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Models.Doctor;
import com.example.medproject.Models.Medication;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    public boolean noMedicationsToDisplay = true;
    public final boolean loggedAsDoctor;
    private final String currentUser;
    private final ArrayList<Medication> medications;
    private final String patientIdCopy;

    public MedicationAdapter(String patientId, String doctorId) {
        patientIdCopy = patientId;
        loggedAsDoctor = patientId != null;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String idToSearchMedication = loggedAsDoctor ? patientId : currentUser;
        boolean displayOnlyOneDoctorsMedication = doctorId != null && !Objects.equals(doctorId, "");
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("PatientToMedications/" + idToSearchMedication, l);
        DatabaseReference mDatabaseReference = FirebaseUtil.mDatabaseReference;
        medications = FirebaseUtil.mMedications;
        mDatabaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Medication medication = dataSnapshot.getValue(Medication.class);
                medication.setId(dataSnapshot.getKey());
                boolean addMedicationToArray = !displayOnlyOneDoctorsMedication || medication.getDoctorId().equals(doctorId);
                if(addMedicationToArray) {
                    noMedicationsToDisplay = false;
                    MyMedications.displayMessageOrMedicationsList();
                    medications.add(medication);
                    notifyItemInserted(medications.size() - 1);
                }
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
                .inflate(R.layout.card_view, parent, false);

        return new MedicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medications.get(position);
        holder.bind(medication);
        if(!loggedAsDoctor)
            holder.deleteMedicationButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }


    public class MedicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView diagnostic;
        private final TextView numeDoctor;
        private CircleImageView medicationIcon;
        private MaterialButton deleteMedicationButton;

        private boolean canEditMedicationFlag = false;

        MedicationViewHolder(View itemView) {
            super(itemView);

            diagnostic = itemView.findViewById(R.id.cardView_title);
            numeDoctor = itemView.findViewById(R.id.cardView_subtitle);
            medicationIcon = itemView.findViewById(R.id.cardView_icon);

            deleteMedicationButton = itemView.findViewById(R.id.cardView_button);
            deleteMedicationButton.setIconResource(R.drawable.ic_delete_black_24dp);
            deleteMedicationButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void bind(Medication medication) {
            deleteMedicationButton.setVisibility(View.INVISIBLE);
            diagnostic.setText(medication.getDiagnostic());
            numeDoctor.setText(medication.getDoctorName());

            String imageUrl = ResourcesHelper.ICONS.get("defaultMedicationIconURL");
            if(medication.getDoctorSpecialization() != null) {
                String helperUrl = ResourcesHelper.ICONS.get(medication.getDoctorSpecialization());
                if(helperUrl != null)
                    imageUrl = helperUrl;
            }
            Picasso.get()
                    .load(imageUrl)
                    .into(medicationIcon);

            if(loggedAsDoctor) {
                canEdit(medication.getDoctorName());
            }
        }

        void canEdit(final String numeDoctor) {
            DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference("Doctors");

            doctorsRef.child(currentUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    String name = doctor.getName();
                    if(name.equals(numeDoctor)) {
                        deleteMedicationButton.setVisibility(View.VISIBLE);
                        canEditMedicationFlag = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            final Medication selectedMedication = medications.get(position);
            final View context = view;

            if (view.getId() == R.id.cardView_button) {
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Ștergere " + selectedMedication.getDiagnostic())
                        .setMessage("Sunteți sigur că doriți să ștergeți această medicație?")
                        .setNegativeButton("Anulare", /* listener = */ null)
                        .setPositiveButton("Ștergere", (dialog, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("PatientToMedications")
                                    .child(patientIdCopy)
                                    .child(selectedMedication.getId())
                                    .removeValue();

                            databaseReference.child("Medications")
                                    .child(selectedMedication.getId())
                                    .removeValue();

                            BasicActions.displaySnackBar(context, "Medicația " + selectedMedication.getDiagnostic() + " a fost ștearsă cu succes");
                        }).show();
            } else {
                String medicationID = selectedMedication.getId();
                Intent intent = new Intent(view.getContext(), MyDrugs.class);
                intent.putExtra("diagnostic", selectedMedication.getDiagnostic());
                intent.putExtra("MedicationID", medicationID);
                intent.putExtra("canEditMedicationFlag", canEditMedicationFlag);
                intent.putExtra("loggedAsDoctor", loggedAsDoctor);
                view.getContext().startActivity(intent);
            }
        }
    }
}

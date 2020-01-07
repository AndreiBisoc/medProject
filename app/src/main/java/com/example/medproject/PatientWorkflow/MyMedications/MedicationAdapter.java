package com.example.medproject.PatientWorkflow.MyMedications;

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

import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.MyPatientsActivity;
import com.example.medproject.PatientWorkflow.MyDrugs.MyDrugs;
import com.example.medproject.R;
import com.example.medproject.data.model.Medication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    public boolean noMedicationsToDisplay = true;
    private ArrayList<Medication> medications;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public MedicationAdapter(String patientId) {
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String idToSearchMedication = patientId != null ? patientId : currentUser;
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("PatientToMedications/" + idToSearchMedication, l);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        medications = FirebaseUtil.mMedications;
        mChildListener = new ChildEventListener() {

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
        };
        mDatabaseReference.addChildEventListener(mChildListener);
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
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }


    public class MedicationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView diagnostic, numeDoctor;
        private Button deleteIcon;

        public MedicationViewHolder(View itemView) {
            super(itemView);
            diagnostic = itemView.findViewById(R.id.medicationDiagnostic);
            numeDoctor = itemView.findViewById(R.id.doctorName);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Medication medication) {
            diagnostic.setText(medication.getDiagnostic());
            numeDoctor.setText(medication.getDoctorName());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Medication selectedMedication = medications.get(position);

            switch (view.getId()) {
                case R.id.deleteIcon:
                    Intent intent = new Intent(view.getContext(), DeleteMedicationPopupActivity.class);
                    intent.putExtra("Medication", selectedMedication);
                    view.getContext().startActivity(intent);
                    break;

                default:
                    String medicationID = selectedMedication.getId();
                    intent = new Intent(view.getContext(), MyDrugs.class);
                    intent.putExtra("MedicationID", medicationID);
                    view.getContext().startActivity(intent);

            }
        }
    }
}

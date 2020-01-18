package com.example.medproject.PatientWorkflow.MyDrugs;

import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.PatientWorkflow.DrugDetails.DrugDetailsAndAdministration;
import com.example.medproject.R;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.MedicationLink;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {

    private ArrayList<MedicationLink> medicationLink;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public DrugAdapter(String medicationID) {
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("Medications/" + medicationID, l);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        medicationLink = FirebaseUtil.mMedicationLink;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    MedicationLink medLink = dataSnapshot.getValue(MedicationLink.class);
                    medLink.setId(dataSnapshot.getKey());
                    medicationLink.add(medLink);
                    notifyItemInserted(DrugAdapter.this.medicationLink.size() - 1);
                }catch (Exception e){

                }
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
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.drug, parent, false);

        return new DrugViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        MedicationLink medLink = medicationLink.get(position);
        holder.bind(medLink);
    }

    @Override
    public int getItemCount() {
        return medicationLink.size();
    }

    public class DrugViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView drugName, drugScop, drugUnitate;

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            drugName = itemView.findViewById(R.id.drugName);
            drugScop = itemView.findViewById(R.id.drugScop);
            drugUnitate = itemView.findViewById(R.id.drugUnitate);
            itemView.setOnClickListener(this);
        }

        public void bind(MedicationLink medLink){
            String drugNameString = medLink.getDrugName();
            drugName.setText(drugNameString);
            final DatabaseReference drugsRef = FirebaseDatabase.getInstance().getReference().child("Drugs");
            drugsRef.orderByChild("nume").equalTo(drugNameString).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot drugData) {
                    Object o = drugData.getValue();
                    String codMedicament = ((HashMap)o).keySet().toString();
                    codMedicament = codMedicament.substring(1);
                    codMedicament= codMedicament.substring(0, codMedicament.length() - 1);
                    drugsRef.child(codMedicament).addValueEventListener(new ValueEventListener(){
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Data is ordered by increasing height, so we want the first entry
                            Drug drug = dataSnapshot.getValue(Drug.class);
                            drugScop.setText(drug.getScop());
                            drugUnitate.setText(drug.getUnitate());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String drugID = medicationLink.get(position).getId();
            String drugAdministrationID = medicationLink.get(position).getDrugAdministration();
            Intent intent = new Intent(view.getContext(), DrugDetailsAndAdministration.class);
            intent.putExtra("drugID", drugID);
            intent.putExtra("drugAdministrationID", drugAdministrationID);
            view.getContext().startActivity(intent);
        }
    }

}
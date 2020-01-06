package com.example.medproject.PatientWorkflow.MyDrugs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.R;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.Medication;
import com.example.medproject.data.model.MedicationLink;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {

    private ArrayList<Drug> drugs;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public DrugAdapter(String medicationID) {
        final ListActivity l = new ListActivity();
        FirebaseUtil.openFbReference("Medications/" + medicationID, l);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        drugs = FirebaseUtil.mDrugs;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MedicationLink medicationLink = dataSnapshot.getValue(MedicationLink.class);
                Drug drug = new Drug(medicationLink.getName());
                drug.setId(dataSnapshot.getKey());
                drugs.add(drug);
                notifyItemInserted(drugs.size() - 1);
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
        Drug drug = drugs.get(position);
        holder.bind(drug);
    }

    @Override
    public int getItemCount() {
        return drugs.size();
    }

    public class DrugViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvNume;

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNume = itemView.findViewById(R.id.tvNume);
            itemView.setOnClickListener(this);
        }

        public void bind(Drug drug){
            tvNume.setText(drug.getNume());
        }

        @Override
        public void onClick(View view) {
        }
    }

}
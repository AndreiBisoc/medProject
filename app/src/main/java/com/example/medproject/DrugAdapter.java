package com.example.medproject;

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

import com.example.medproject.AdministratorWorkflow.AddDrug;
import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.data.Models.Drug;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder>{

    private final ArrayList<Drug> drugs;

    public DrugAdapter(){
        //FirebaseUtil.openFbReference("Drugs");
        FirebaseDatabase mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        DatabaseReference mDatabaseReference = FirebaseUtil.mDatabaseReference;
        drugs = FirebaseUtil.mDrugs;
        ChildEventListener mChildListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Drug td = dataSnapshot.getValue(Drug.class);
                Log.d("Drug: ", td.getNume());
                td.setId(dataSnapshot.getKey());
                drugs.add(td);
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
                .inflate(R.layout.rv_row, parent, false);

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
        final TextView tvNume;
        final TextView tvScop;
        final TextView tvUnitate;
        DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNume = itemView.findViewById(R.id.drugName);
            tvScop = itemView.findViewById(R.id.drugScop);
            tvUnitate = itemView.findViewById(R.id.drugUnitate);
            itemView.setOnClickListener(this);
        }

        void bind(Drug drug){
            tvNume.setText(drug.getNume());
            tvScop.setText(drug.getScop());
            tvUnitate.setText(drug.getUnitate());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            Drug selectedDrug = drugs.get(position);
            Intent intent = new Intent(view.getContext(), AddDrug.class);
            intent.putExtra("Drug", selectedDrug);
            view.getContext().startActivity(intent);
        }
    }

}

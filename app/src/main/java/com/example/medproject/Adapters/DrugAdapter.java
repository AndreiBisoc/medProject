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

import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.PatientWorkflow.SeeDrugDetailsAndAdministration;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Models.Drug;
import com.example.medproject.Models.MedicationLink;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {

    private final ArrayList<MedicationLink> medicationLink;
    private FirebaseDatabase mFirebaseDatabase;
    private final boolean canEditMedicationFlag;
    private final boolean loggedAsDoctor;

    public DrugAdapter(String medicationID, boolean canEditMedicationFlag, boolean loggedAsDoctor) {
        this.canEditMedicationFlag = canEditMedicationFlag;
        this.loggedAsDoctor = loggedAsDoctor;
        FirebaseUtil.openFbReference("Medications/" + medicationID);
        DatabaseReference mDatabaseReference = FirebaseUtil.mDatabaseReference;
        medicationLink = FirebaseUtil.mMedicationLink;
        ChildEventListener mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    MedicationLink medLink = dataSnapshot.getValue(MedicationLink.class);
                    if (medLink != null) {
                        medLink.setId(dataSnapshot.getKey());
                    }
                    medicationLink.add(medLink);
                    notifyItemInserted(DrugAdapter.this.medicationLink.size() - 1);
                } catch (Exception ignored) {

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
                .inflate(R.layout.card_view, parent, false);

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
        final TextView drugNameAndUnit;
        final TextView drugScop;
        final CircleImageView drugIcon;

        DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            drugNameAndUnit = itemView.findViewById(R.id.cardView_title);
            drugScop = itemView.findViewById(R.id.cardView_subtitle);
            drugIcon = itemView.findViewById(R.id.cardView_icon);
            itemView.findViewById(R.id.cardView_button).setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        void bind(MedicationLink medLink){
            String defaultDrugIconURL = ResourcesHelper.ICONS.get("defaultDrugIconURL");
            Picasso.get()
                    .load(defaultDrugIconURL)
                    .into(drugIcon);
            String drugNameString = medLink.getDrugName();
            final DatabaseReference drugsRef = FirebaseDatabase.getInstance().getReference().child("Drugs");
            drugsRef.orderByChild("nume").equalTo(drugNameString).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot drugData) {
                    Object o = drugData.getValue();
                    assert o != null;
                    String codMedicament = ((HashMap)o).keySet().toString();
                    codMedicament = codMedicament.substring(1);
                    codMedicament= codMedicament.substring(0, codMedicament.length() - 1);
                    drugsRef.child(codMedicament).addValueEventListener(new ValueEventListener(){
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Data is ordered by increasing height, so we want the first entry
                            Drug drug = dataSnapshot.getValue(Drug.class);
                            assert drug != null;
                            drugScop.setText(drug.getScop());
                            drugNameAndUnit.setText(String.format(Locale.forLanguageTag("ro_RO"),"%s", drugNameString + ", " + drug.getUnitate()));
                            String unit = drug.getUnitate();
                            String imageUrl = defaultDrugIconURL;
                            if(unit.equals("ml.")) {
                                imageUrl = ResourcesHelper.ICONS.get("DrugSyrupIconURL");
                            } else if (unit.equals("comprimate")) {
                                imageUrl = ResourcesHelper.ICONS.get("DrugPillsIconURL");
                            }

                            Picasso.get()
                                    .load(imageUrl)
                                    .into(drugIcon);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // ...
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // ...
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String drugID = medicationLink.get(position).getId();
            String drugAdministrationID = medicationLink.get(position).getDrugAdministration();
            Intent intent = new Intent(view.getContext(), SeeDrugDetailsAndAdministration.class);
            intent.putExtra("drugID", drugID);
            intent.putExtra("drugAdministrationID", drugAdministrationID);
            intent.putExtra("canEditMedicationFlag", canEditMedicationFlag);
            intent.putExtra("loggedAsDoctor", loggedAsDoctor);
            view.getContext().startActivity(intent);
        }
    }

}
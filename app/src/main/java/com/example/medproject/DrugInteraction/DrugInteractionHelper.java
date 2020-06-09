package com.example.medproject.DrugInteraction;

import androidx.annotation.NonNull;

import com.example.medproject.Models.Drug;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DrugInteractionHelper {

    public static int[] getInteractionFromDatabase(String drug1, String drug2) {

        DatabaseReference drugsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drugs");
        final Drug[] foundDrug = new Drug[1];
        int[] interaction = new int[2];
        drugsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drug drug = snapshot.getValue(Drug.class);
                    String drugName = drug.getNume().toLowerCase();
                    if(drugName.equals(drug1)) {
                        if(drug.getNegativeInteractingDrugs() != null) {
                            interaction[0] = checkLinkBetweenDrugs(drug.getNegativeInteractingDrugs(), drug2) * (-1);
                        }
                        if(interaction[0] == 0 && drug.getPositiveInteractingDrugs() != null) {
                            interaction[0] = checkLinkBetweenDrugs(drug.getPositiveInteractingDrugs(), drug2);
                        }
                        if (drug.getDrugsWithHumanBodyEffect() != null) {
                            interaction[1] = checkLinkBetweenDrugs(drug.getDrugsWithHumanBodyEffect(), drug2) * (-1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return interaction;
    }

    private static int checkLinkBetweenDrugs(String[] array, String drug2) {
        List<String> interactingDrugs = Arrays.asList(array)
                .stream()
                .map(s -> s.toLowerCase()).collect(Collectors.toList());
        if (interactingDrugs.contains(drug2.toLowerCase())) {
            return 1;
        }
        return 0;
    }

}
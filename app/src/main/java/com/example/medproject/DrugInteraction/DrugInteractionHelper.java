package com.example.medproject.DrugInteraction;

import androidx.annotation.NonNull;

import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.Models.Drug;
import com.example.medproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrugInteractionHelper {

    public static int[] getInteractionFromDatabase(String drug1, String drug2) {

        DatabaseReference drugsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drugs");
        int[] interaction = new int[2];
        drugsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drug drug = snapshot.getValue(Drug.class);
                    String drugName = drug.getNume();
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

    public static String getMedicineWhoseCombinationAffectsHumanBody(String drug1, String allDrugsOfThatPatient) {
        String[] medicineName = new String[1];
        FirebaseUtil.openFbReference("Drugs");
        DatabaseReference drugsDatabaseReference = FirebaseUtil.mDatabaseReference;
        drugsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drug drug = snapshot.getValue(Drug.class);
                    if(drug.getNume().equals(drug1)) {
                        if (drug.getDrugsWithHumanBodyEffect() != null) {
                            List<String> drugsAdministrated = convertToList(allDrugsOfThatPatient.split(","));
                            List<String> interactingDrugs = convertToList(drug.getDrugsWithHumanBodyEffect());
                            for(String interactingDrug : interactingDrugs) {
                                if(drugsAdministrated.contains(interactingDrug)) {
                                    medicineName[0]=interactingDrug;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return medicineName[0];
    }

    public static List<String> convertToList(String[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    private static int checkLinkBetweenDrugs(String[] array, String drug2) {
        List<String> interactingDrugs = convertToList(array);
        if (interactingDrugs.contains(drug2)) {
            return 1;
        }
        return 0;
    }

    public static int getInteractionColor(int interactionPositivity) {
        if(interactionPositivity > 0) {
            return R.color.forestgreen;
        } else {
            if(interactionPositivity < 0)
                return R.color.red;
            else
                return R.color.amber;
        }
    }

    public static int getInteractionIcon(int interactionPositivity) {
        if(interactionPositivity > 0) {
            return R.drawable.ic_check_24dp;
        } else {
            if(interactionPositivity < 0)
                return R.drawable.ic_warning_24dp;
            else
                return R.drawable.ic_not_found_24dp;
        }
    }

//    public static String getPatientDrugs(String patientId) {
//        final StringBuilder allDrugs = new StringBuilder();
//        if(patientId == null)
//            return allDrugs.toString();
//        DatabaseReference patientToMedicationsDBRef = FirebaseDatabase.getInstance().getReference().child("PatientToMedications").child(patientId);
//        patientToMedicationsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot medication : dataSnapshot.getChildren()){
//                    allDrugs.append(medication.child("drugList").getValue().toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        return allDrugs.toString();
//    }
}
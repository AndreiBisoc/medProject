package com.example.medproject.GeneralActivities;

import com.example.medproject.Models.Doctor;
import com.example.medproject.Models.Drug;
import com.example.medproject.Models.Medication;
import com.example.medproject.Models.MedicationLink;
import com.example.medproject.Models.Patient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {
    private static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;

    private static ArrayList<Drug> mDrugs;
    public static ArrayList<Doctor> mDoctors;
    private static ArrayList<Patient> mPatients;
    public static ArrayList<Medication> mMedications;
    public static ArrayList<MedicationLink> mMedicationLink;

    private FirebaseUtil(){}

    public static void openFbReference(String ref){
        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        }

        mDrugs = new ArrayList<>();
        mDoctors = new ArrayList<>();
        mPatients = new ArrayList<>();
        mMedications = new ArrayList<>();
        mMedicationLink = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}

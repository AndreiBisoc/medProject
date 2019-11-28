package com.example.medproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<Drug> mDrugs;

    private FirebaseUtil(){};

    public static void openFbReference(String ref){
        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDrugs = new ArrayList<Drug>();
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
}

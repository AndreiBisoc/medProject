package com.example.medproject;

import android.app.Activity;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.data.model.Exceptions.DoctorNotLinkedToPatientException;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BasicActions {

    public static void hideKeyboard(AppCompatActivity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardWithClick(View element, final AppCompatActivity activity) {
        element.setOnTouchListener((v, event) -> {
            hideKeyboard(activity);
            return true;
        });
    }

    public static void hideActionBar(AppCompatActivity activity){
        try
        {
            activity.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
    }

    public static void displaySnackBar(View context, String message) {
        Snackbar.make(context, message, Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#ffb300"))
                .show();
    }

    public static void checkDoctorPatientLink(String doctorId, String patientId) {
        DatabaseReference doctorToPatientLinkRef = FirebaseDatabase.getInstance().getReference("DoctorsToPatients").child(doctorId).child(patientId);

        doctorToPatientLinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    throw new DoctorNotLinkedToPatientException();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

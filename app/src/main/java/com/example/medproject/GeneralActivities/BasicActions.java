package com.example.medproject.GeneralActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.DrugInteraction.DrugInteraction;
import com.example.medproject.MyAccount.ProfileDetails;
import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.R;
import com.example.medproject.Models.Exceptions.DoctorNotLinkedToPatientException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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

    public static void displaySnackBarAndFinish(Activity activity, String message) {
        Snackbar.make(activity.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#ffb300"))
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        activity.finish();
                    }
                })
                .show();
    }

    public static void displaySnackBarLonger(View context, String message) {
        Snackbar.make(context, message, Snackbar.LENGTH_LONG)
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

    public static void checkIfUserIsLogged(AppCompatActivity activity) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            activity.finish();
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    public static void manageNavigationView(AppCompatActivity activity, BottomNavigationView bottomNavigationView, boolean loggedAsDoctor) {
        if(loggedAsDoctor) {
            bottomNavigationView.getMenu().removeItem(R.id.my_medications);
            bottomNavigationView.getMenu().removeItem(R.id.my_doctors);
        }
        else {
            bottomNavigationView.getMenu().removeItem(R.id.my_patients);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()){
                case R.id.my_medications:
                    if(!activity.getClass().equals(MyMedications.class)) {
                        activity.finish();
                        intent = new Intent(activity.getApplicationContext(), MyMedications.class);
                        intent.putExtra("loggedAsDoctor", loggedAsDoctor);
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.study_drug_interactions:
                    if(!activity.getClass().equals(DrugInteraction.class)) {
                        activity.finish();
                        intent = new Intent(activity.getApplicationContext(), DrugInteraction.class);
                        intent.putExtra("loggedAsDoctor", loggedAsDoctor);
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.my_patients:
                    if(!activity.getClass().equals(MyPatientsOrMyDoctorsActivity.class)) {
                        activity.finish();
                        intent = new Intent(activity.getApplicationContext(), MyPatientsOrMyDoctorsActivity.class);
                        intent.putExtra("loggedAsDoctor", true);
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.my_doctors:
                    if(!activity.getClass().equals(MyPatientsOrMyDoctorsActivity.class)) {
                        activity.finish();
                        intent = new Intent(activity.getApplicationContext(), MyPatientsOrMyDoctorsActivity.class);
                        intent.putExtra("loggedAsDoctor", false);
                        activity.startActivity(intent);
                    }
                    break;
                case R.id.my_profile:
                    if(!activity.getClass().equals(ProfileDetails.class)) {
                        activity.finish();
                        intent = new Intent(activity.getApplicationContext(), ProfileDetails.class);
                        intent.putExtra("loggedAsDoctor", loggedAsDoctor);
                        activity.startActivity(intent);
                    }
                    break;
            }
            return false;
        });
    }

}

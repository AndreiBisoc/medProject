package com.example.medproject.QRCode.PatientQRCode;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.Models.DoctorToPatientLink;
import com.example.medproject.Models.Exceptions.NotAPatientAccountException;
import com.example.medproject.Models.Patient;
import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanPatientId extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_scan_qr);

//        Init
        TextView emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.description_scan_patient_QR_code);
        scannerView = findViewById(R.id.zxscan);

//        Request Permission for using camera
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(ScanPatientId.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        BasicActions.displaySnackBar(getWindow().getDecorView(), "Trebuie să acceptați utilizarea camerei.");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onDestroy(){
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        String patientId = rawResult.getText();
        try {
            String loggedDoctorId = FirebaseAuth.getInstance().getUid();
            addDoctorToPatientLink(loggedDoctorId, patientId);
        } catch (NotAPatientAccountException e) {
            BasicActions.displaySnackBar(getWindow().getDecorView(), e.toString());
        }
    }

    private void addDoctorToPatientLink(final String doctorId, final String patientId) {

        String registerDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
        FirebaseDatabase.getInstance().getReference("Patients")
                .child(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot == null) {
                            BasicActions.displaySnackBar(getWindow().getDecorView(), "Codul scanat nu e valid. Nu corespunde niciunui pacient.");
                            throw new NotAPatientAccountException();
                        } else {
                            Patient patientToAdd = snapshot.getValue(Patient.class);
                            DoctorToPatientLink doctorToPatientLink = new DoctorToPatientLink(patientToAdd, registerDate);
                            ArrayList<String> patientsCNPs = getIntent().getStringArrayListExtra("patientsCNPs");
                            if(patientsCNPs.contains(patientToAdd.getCNP())){
                                BasicActions.displaySnackBarLonger(getWindow().getDecorView(), "Pacientul există deja în lista dumneavoastră.");
                                goToMyPatientsActivity();
                            }
                            else {
                                FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                                        .child(doctorId)
                                        .child(patientId)
                                        .setValue(doctorToPatientLink).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        BasicActions.displaySnackBar(getWindow().getDecorView(), "Pacientul a fost adăugat cu succes");
                                        goToMyPatientsActivity();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void goToMyPatientsActivity() {
        Intent toMyPatientsActivity = new Intent(getApplicationContext(), MyPatientsOrMyDoctorsActivity.class);
        toMyPatientsActivity.putExtra("loggedAsDoctor", true);
        startActivity(toMyPatientsActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        menu.removeItem(R.id.insert_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent toLoginPage = new Intent(this, LoginActivity.class);
            toLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toLoginPage.putExtra("logOut", "logOut");
            startActivity(toLoginPage);
        }
        return true;
    }

}

package com.example.medproject.QRCode.MedicationQRCode;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.Notifications.ReminderBroadcast;
import com.example.medproject.PatientWorkflow.MyMedications.MyMedications;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.DrugAdministration;
import com.example.medproject.data.model.Exceptions.DoctorNotLinkedToPatientException;
import com.example.medproject.data.model.Exceptions.WrongPatientScanningQRException;
import com.example.medproject.data.model.Medication;
import com.example.medproject.data.model.MedicationAdministration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanMedicationId extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private final List<MedicationAdministration> medicationAdministrationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

//        Init
        scannerView = findViewById(R.id.zxscan);

//        Request Permission for using camera
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(ScanMedicationId.this);
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
        final String scannedInfo = rawResult.getText();
        String[] info = scannedInfo.split(":");
        String doctorId = info[0];
        String patientId = info[1];
        String medicationId = info[2];

        try {
            BasicActions.checkDoctorPatientLink(doctorId, patientId);
            String loggedPatientId = FirebaseAuth.getInstance().getUid();
            if(!loggedPatientId.equals(patientId)) {
                throw new WrongPatientScanningQRException();
            }
            addMedicationToDb(patientId, medicationId);
        } catch (DoctorNotLinkedToPatientException e) {
            BasicActions.displaySnackBar(getWindow().getDecorView(), e.toString());
        } catch (WrongPatientScanningQRException e) {
            BasicActions.displaySnackBar(getWindow().getDecorView(), e.toString());
        } finally {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                finish();
                goToMyMedicationPage();
            }, 500);

        }

    }

    private void goToMyMedicationPage(){
        Intent intent = new Intent(this, MyMedications.class);
        intent.putExtra("loggedAsDoctor", false);
        startActivity(intent);
    }

    private void addMedicationToDb(final String patientId, final String medicationId) {

        DatabaseReference medicationsDbRef = FirebaseDatabase.getInstance().getReference("Medications/" + medicationId);
        medicationsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {     // medicația pe care o salvezi ulterior în patient to medication
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(!Objects.equals(postSnapshot.getKey(), "diagnostic") && !Objects.equals(postSnapshot.getKey(), "doctorName" ) && !Objects.equals(postSnapshot.getKey(), "doctorSpecialization") ) {
                        MedicationAdministration med = postSnapshot.getValue(MedicationAdministration.class);
                        med.setDrugId(postSnapshot.getKey());
                        medicationAdministrationList.add(med);
                    }
                }
                Medication medication = dataSnapshot.getValue(Medication.class);
                if(medication != null) {
                    DatabaseReference patientToMedicationsDbRef = FirebaseDatabase.getInstance().getReference("PatientToMedications")
                            .child(patientId)
                            .child(medicationId);
                    patientToMedicationsDbRef.setValue(medication);
                }

                handleNotifications();
                BasicActions.displaySnackBar(getWindow().getDecorView(), "Notificarile au fost setate cu succes");
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleNotifications(){
        createNotificationChannel();

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("DrugAdministration");
        for (MedicationAdministration medAdministration : medicationAdministrationList) {
            mDatabaseReference.child(medAdministration.getDrugAdministration()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DrugAdministration drugAdministration = dataSnapshot.getValue(DrugAdministration.class);
                    createNotifications(drugAdministration, medAdministration.getDrugName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNotifications(DrugAdministration drugAdministration, String drugName){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date now = Calendar.getInstance().getTime();
            Date startingDate = dateFormat.parse(drugAdministration.getStartDay() + " "
                    + drugAdministration.getStartHour() + ":00");

            int nrOfTimesPerDay = Integer.parseInt(drugAdministration.getNoOfTimes().replaceAll("[^0-9]", ""));

            int totalTimes = Integer.parseInt(drugAdministration.getNoOfDays()) *
                    nrOfTimesPerDay;

            long timeToNextAlarm = (24 * 60 * 60 * 1000)  / nrOfTimesPerDay ;

            Intent broadcastIntent = new Intent(this, ReminderBroadcast.class);
            broadcastIntent.putExtra("totalTimes", totalTimes);
            broadcastIntent.putExtra("index",  1);
            broadcastIntent.putExtra("timeToNextAlarm", timeToNextAlarm);
            broadcastIntent.putExtra("drugName", drugName);
            int dosage = Integer.parseInt(drugAdministration.getDosage());
            broadcastIntent.putExtra("dosage", dosage);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    (int) now.getTime(), broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC,
                    startingDate.getTime(),
                    pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PillReminderChannel";
            String description = "Channel for pill reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("takePill", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

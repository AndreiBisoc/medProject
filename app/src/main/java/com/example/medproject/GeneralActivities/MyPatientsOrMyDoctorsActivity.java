package com.example.medproject.GeneralActivities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.Adapters.DoctorAdapter;
import com.example.medproject.Adapters.PatientAdapter;
import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.QRCode.PatientQRCode.GeneratePatientQRCode;
import com.example.medproject.QRCode.PatientQRCode.ScanPatientId;
import com.example.medproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MyPatientsOrMyDoctorsActivity extends AppCompatActivity {

    private static RecyclerView rvList;
    private static TextView emptyView;
    private static PatientAdapter PATIENT_ADAPTER;
    private static DoctorAdapter DOCTOR_ADAPTER;
    private boolean loggedAsDoctor;
    //private ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_my_patients);

        loggedAsDoctor = getIntent().getBooleanExtra("loggedAsDoctor", false);

        rvList = findViewById(R.id.rvList);

        if(loggedAsDoctor) {
            PATIENT_ADAPTER = new PatientAdapter();
//            loggedAsDoctor needs Patient Adapter because the doctor sees his list of patients
            rvList.setAdapter(PATIENT_ADAPTER);
            initializePage("Pacienții mei");
        } else {
            DOCTOR_ADAPTER = new DoctorAdapter(loggedAsDoctor);
            rvList.setAdapter(DOCTOR_ADAPTER);
            initializePage("Doctorii mei");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);

        //progressBar = findViewById(R.id.progressBar);
    }

    private void initializePage(String title) {
        setTitle(title);

        emptyView = findViewById(R.id.empty_view);

        LinearLayoutManager listLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(listLayoutManager);

        displayMessageOrList(loggedAsDoctor);

        ExtendedFloatingActionButton addPatientToDoctor = findViewById(R.id.addPatientToDoctorButton);
        addPatientToDoctor.setOnClickListener(v -> goToAddPatientPage());
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

    private void goToAddPatientPage(){
        if(loggedAsDoctor) {
            Intent addNewPatient = new Intent(this, ScanPatientId.class);
            addNewPatient.putExtra("patientsCNPs", PATIENT_ADAPTER.patientsCNPs);
            startActivity(addNewPatient);
        } else {
            Intent addNewDoctor = new Intent(this, GeneratePatientQRCode.class);
            startActivity(addNewDoctor);
        }
    }

    public static void displayMessageOrList(boolean loggedAsDoctor) {
        boolean listIsEmpty = loggedAsDoctor ? PATIENT_ADAPTER.noPatientsToDisplay : DOCTOR_ADAPTER.noDoctorsToDisplay;
        if(listIsEmpty)
        {
            rvList.setVisibility(View.GONE);
            if(!loggedAsDoctor) {
                emptyView.setText(R.string.no_doctor_to_display);
            }
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rvList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

//    private void disableControllers(boolean isEnabled){
//        addPatientToDoctor.setEnabled(!isEnabled);
//    }
}

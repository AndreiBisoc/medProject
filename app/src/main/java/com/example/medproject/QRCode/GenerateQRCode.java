package com.example.medproject.QRCode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.DoctorWorkflow.MyPacients.MyPatientsActivity;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class GenerateQRCode extends AppCompatActivity {

    private Button encodeButton, backButton;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        progressBar = findViewById(R.id.progressBar);
        encodeButton = findViewById(R.id.generateCode);
        backButton = findViewById(R.id.backToMyPatients);
        imageView = findViewById(R.id.qrCode);
        backButton.setVisibility(View.GONE);

        encodeButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);
            try {
                String medicationId = getIntent().getStringExtra("medicationId");
                String patientId = getIntent().getStringExtra("patientId");
                if(medicationId == null || patientId ==null) {
                    throw new NullPointerException();
                }
                String qrData = getDataToEncode(patientId, medicationId);
                encodeButton.setVisibility(View.GONE);
                new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + qrData);
                backButton.setVisibility(View.VISIBLE);
            } catch(NullPointerException e) {
                BasicActions.displaySnackBar(getWindow().getDecorView(), "Eroare la completarea medicației");
        }
            progressBar.setVisibility(View.GONE);
            disableControllers(false);
        });

        backButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);
            finish();
            Intent intent = new Intent(getApplicationContext(), MyPatientsActivity.class);
            intent.putExtra("loggedAsDoctor", true);
            startActivity(intent);
        });
    }

    private String getDataToEncode(String patientId, String medicationId) {
        String doctorId = FirebaseAuth.getInstance().getUid();
        return doctorId+ ":" + patientId + ":" + medicationId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        menu.removeItem(R.id.insert_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_account:
                startActivity(new Intent(this, DoctorDetails.class));
                break;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            Log.d("Logout","Persoana a fost delogată!");
                            FirebaseUtil.attachListener();
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disableControllers(boolean isEnabled){
        backButton.setEnabled(!isEnabled);
        encodeButton.setEnabled(!isEnabled);
    }
}

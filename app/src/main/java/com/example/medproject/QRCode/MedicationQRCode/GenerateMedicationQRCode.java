package com.example.medproject.QRCode.MedicationQRCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.QRCode.ImageDownloaderTask;
import com.example.medproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class GenerateMedicationQRCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_generate_qrcode);
        ImageView imageView = findViewById(R.id.qrCode);

        try {
            String medicationId = getIntent().getStringExtra("medicationId");
            String patientId = getIntent().getStringExtra("patientId");
            if(medicationId == null || patientId ==null) {
                throw new NullPointerException();
            }
            String qrData = getDataToEncode(patientId, medicationId);
            new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + qrData);
        } catch(NullPointerException e) {
            BasicActions.displaySnackBar(getWindow().getDecorView(), "Eroare la completarea medicației");
        }
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

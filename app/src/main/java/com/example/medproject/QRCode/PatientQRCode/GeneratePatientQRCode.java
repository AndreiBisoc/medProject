package com.example.medproject.QRCode.PatientQRCode;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.BasicActions;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.QRCode.ImageDownloaderTask;
import com.example.medproject.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class GeneratePatientQRCode extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.activity_generate_qrcode);

        imageView = findViewById(R.id.qrCode);
        TextView emptyView = findViewById(R.id.empty_view);
        emptyView.setText(R.string.description_generate_patient_QR_code);

        generateQRImage();
    }

    private void generateQRImage() {
        String qrData = getDataToEncode();
        new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + qrData);
    }

    private String getDataToEncode() {
        return FirebaseAuth.getInstance().getUid();
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
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        Log.d("Logout", "Persoana a fost delogată!");
                        FirebaseUtil.attachListener();
                    });
            FirebaseUtil.detachListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

package com.example.medproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GenerateQRCode extends AppCompatActivity {

    private EditText stringToEncode;
    private Button encodeButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        stringToEncode = findViewById(R.id.stringToEncode);
        encodeButton = findViewById(R.id.generateCode);
        imageView = findViewById(R.id.qrCode);

        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // aici la noi text va lua id-ul medicatiei ce se va genera in codul QR cu getIntent de la activitatea anterioara
                String text = stringToEncode.getText().toString();
                if(!text.equals("")) {
                    new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + text);
                } else {
                    Toast.makeText(GenerateQRCode.this, "Enter Something!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout","Persoana a fost delogată!");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.medproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
}

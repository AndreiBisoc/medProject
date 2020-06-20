package com.example.medproject.Authentication.Register;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.Models.Doctor;
import com.example.medproject.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class RegisterDoctorActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtPrenume, txtNume, txtTelefon, txtAdresaCabinet;
    private AutoCompleteTextView txtSpecialisation;
    private ProgressBar progressBar;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private final String[] SPECIALISATIONS = new String[] {"Cardiologie", "Chirurgie generală", "Dermatovenerologie", "Hematologie", "Medicină internă",
            "Medicină de familie", "Obstetrică-ginecologie", "Psihiatrie"};

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_doctor_register);
        BasicActions.hideActionBar(this);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        txtPrenume = findViewById(R.id.txtPrenume);
        txtNume = findViewById(R.id.txtNume);
        txtTelefon = findViewById(R.id.phoneNumber);
        txtAdresaCabinet = findViewById(R.id.address);
        ArrayAdapter< String > adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, SPECIALISATIONS);

        txtSpecialisation = findViewById(R.id.specialisation);
        txtSpecialisation.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        registerButton.setEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerButton:
                try {
                    registerUser();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void registerUser() throws ExecutionException, InterruptedException {
        Intent intent = getIntent();

        final String prenume = txtPrenume.getText().toString().trim();
        final String nume = txtNume.getText().toString().trim();
        final String telefon = txtTelefon.getText().toString().trim();
        final String adresaCabinet = txtAdresaCabinet.getText().toString().trim();
        final String specialisation = txtSpecialisation.getText().toString();
        final String email = intent.getStringExtra("EMAIL");
        final String password = intent.getStringExtra("PASSWORD");

        if(validareRegisterPacient(prenume, nume, telefon, adresaCabinet)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Doctor doctor = new Doctor(email, prenume, nume, specialisation, telefon, adresaCabinet);
                        doctor.setId(mAuth.getUid());
                        FirebaseDatabase.getInstance().getReference("Doctors")
                                .child(mAuth.getUid())
                                .setValue(doctor).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                BasicActions.displaySnackBar(getWindow().getDecorView(), "Înregistrarea a avut loc cu succes");
                                finishAffinity();
                                Intent intent1 = new Intent(RegisterDoctorActivity.this, MyPatientsOrMyDoctorsActivity.class);
                                intent1.putExtra("loggedAsDoctor", true);
                                startActivity(intent1);
                            } else {
                                if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Există deja un cont cu acest email");
                                } else {
                                    Toast.makeText(getApplicationContext(), task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterDoctorActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validareRegisterPacient(String prenume, String nume, String telefon, String adresaCabinet) throws ExecutionException, InterruptedException {

        if(prenume.isEmpty()){
            txtPrenume.setError("Introduceți prenumele");
            txtPrenume.requestFocus();
            return true;
        }

        if(nume.isEmpty()){
            txtNume.setError("Introduceți numele");
            txtNume.requestFocus();
            return true;
        }

        if(telefon.isEmpty()){
            txtTelefon.setError("Introduceți nr. de telefon");
            txtTelefon.requestFocus();
            return true;
        }
        if(telefon.length() < 10){
            txtTelefon.setError("Introduceți un nr. de telefon valid");
            txtTelefon.requestFocus();
            return true;
        }

        if(adresaCabinet.isEmpty()){
            txtAdresaCabinet.setError("Introduceți adresa");
            txtAdresaCabinet.requestFocus();
            return true;
        }

        //Verificare in registrul national al doctorilor
        String result = new JsonTask().execute("https://regmed.cmr.ro/api/v1/public/cautare/" + nume + prenume).get();
        String totalResults = result.split(",")[0].replaceAll("[^0-9]", "");
        try {
            int numberOfResults = Integer.parseInt(totalResults);
            if(numberOfResults == 0) {
                txtPrenume.requestFocus();
                txtPrenume.setError("Acest doctor nu există.");
                txtNume.setError("");
                return true;
            }
            JsonObject doctorDetails = new JsonParser().parse(result).getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject();
            String status = doctorDetails.get("status").toString().toLowerCase().replace("\"","");
            String specialitate = doctorDetails.get("specialitati").getAsJsonArray().get(0).getAsJsonObject().get("nume").toString().toLowerCase().replace("\"","");
            if (!status.equals("activ")) {
                txtPrenume.requestFocus();
                txtPrenume.setError("Acest doctor nu mai profesează.");
                txtNume.setError("");
                return true;
            }
            if(!specialitate.equals(txtSpecialisation.getText().toString().toLowerCase())) {
                TextInputLayout specialisationDropDown = findViewById(R.id.specialisationDropDown);
                specialisationDropDown.setEndIconMode(TextInputLayout.END_ICON_NONE);
                txtSpecialisation.requestFocus();
                txtSpecialisation.setError("Acest doctor nu are această specializare.");
                return true;
            }
        } catch(NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return false;
    }

    private void disableControllers(boolean isEnabled){
        txtNume.setEnabled(!isEnabled);
        txtPrenume.setEnabled(!isEnabled);
        txtTelefon.setEnabled(!isEnabled);
        txtAdresaCabinet.setEnabled(!isEnabled);
        txtSpecialisation.setEnabled(!isEnabled);
        registerButton.setEnabled(!isEnabled);
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
        }
    }
}


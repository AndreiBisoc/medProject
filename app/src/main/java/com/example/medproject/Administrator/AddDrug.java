package com.example.medproject.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medproject.BasicActions;
import com.example.medproject.DoctorWorkflow.AddMedication.AddDrugToMedication;
import com.example.medproject.FirebaseUtil;
import com.example.medproject.ListActivity;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Drug;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDrug extends AppCompatActivity implements View.OnClickListener{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtNume, txtScop, txtUnitate, txtDescriere;
    private Drug drug = new Drug();
    private boolean goToAddDrugToMedication = false;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_drug);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Drugs");

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        Intent intentFromDoctor = getIntent();
        String drugName = intentFromDoctor.getStringExtra("drugName");

        txtNume = findViewById(R.id.txtNume);
        if(drugName != null) {
            goToAddDrugToMedication = true;
            txtNume.setText(drugName);
        }
        txtScop = findViewById(R.id.txtScop);
        txtUnitate = findViewById(R.id.txtUnitate);
        txtDescriere = findViewById(R.id.txtDescriere);
        button = findViewById(R.id.addDrugButton);
        button.setOnClickListener(this);
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
        switch(item.getItemId()){
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        saveDrug();
        clean();
    }

    private void saveDrug(){
        drug.setNume(txtNume.getText().toString());
        drug.setScop(txtScop.getText().toString());
        drug.setUnitate(txtUnitate.getText().toString());
        drug.setDescriere(txtDescriere.getText().toString());
        DatabaseReference pushedDrugRef = mDatabaseReference.push();
        pushedDrugRef.setValue(drug);
        String drugId = pushedDrugRef.getKey();
        Toast.makeText(this, "Medicamentul " + drug.getNume() + " a fost salvat", Toast.LENGTH_LONG).show();
        if(goToAddDrugToMedication){
            Intent backToMedication = new Intent(this, AddDrugToMedication.class);
            backToMedication.putExtra("drugId", drugId);
            startActivity(backToMedication);
        }
    }

    private void deleteDrug(){
        if(drug == null){
            Toast.makeText(this,"Vă rugăm salvați medicamentul înainte să îl ștergeți!",Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(drug.getId()).removeValue();
    }

    private void clean() {
        txtNume.setText("");
        txtScop.setText("");
        txtUnitate.setText("");
        txtDescriere.setText("");
    }

    private void enableEditTexts(boolean isEnabled){
        txtNume.setEnabled(isEnabled);
        txtScop.setEnabled(isEnabled);
        txtUnitate.setEnabled(isEnabled);
        txtDescriere.setEnabled(isEnabled);
    }
}

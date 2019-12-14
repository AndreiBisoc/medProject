package com.example.medproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medproject.data.model.Drug;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DrugActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtNume;
    EditText txtScop;
    EditText txtUnitate;
    EditText txtDescriere;
    Drug drug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseUtil.openFbReference("Drugs");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtNume = findViewById(R.id.txtNume);
        txtScop = findViewById(R.id.txtScop);
        txtUnitate = findViewById(R.id.txtUnitate);
        txtDescriere = findViewById(R.id.txtDescriere);

        Intent intent = getIntent();
        Drug drug = (Drug) intent.getSerializableExtra("Drug");
        if(drug == null){
            drug = new Drug();
        }
        this.drug = drug;
        txtNume.setText(drug.getNume());
        txtScop.setText(drug.getScop());
        txtUnitate.setText(drug.getUnitate());
        txtDescriere.setText(drug.getDescriere());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDrug();
                Toast.makeText(this, "Medicamentul a fost salvat", Toast.LENGTH_LONG).show();
                clean();
                backtoList();
                return true;
            case R.id.delete_menu:
                deleteDrug();
                Toast.makeText(this, "Medicamentul a fost șters", Toast.LENGTH_LONG).show();
                clean();
                backtoList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDrug(){
        drug.setNume(txtNume.getText().toString());
        drug.setScop(txtScop.getText().toString());
        drug.setUnitate(txtUnitate.getText().toString());
        drug.setDescriere(txtDescriere.getText().toString());
        if(drug.getId() == null) {
            mDatabaseReference.push().setValue(drug);
        }
        else{
            mDatabaseReference.child(drug.getId()).setValue(drug);
        }
    }

    private void deleteDrug(){
        if(drug == null){
            Toast.makeText(this,"Vă rugăm salvați medicamentul înainte să îl ștergeți!",Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(drug.getId()).removeValue();
    }

    private void backtoList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
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

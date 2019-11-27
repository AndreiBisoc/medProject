package com.example.medproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtNume;
    EditText txtScop;
    EditText txtUnitate;
    EditText txtDescriere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Drugs");

        txtNume = (EditText) findViewById(R.id.txtNume);
        txtScop = (EditText) findViewById(R.id.txtScop);
        txtUnitate = (EditText) findViewById(R.id.txtUnitate);
        txtDescriere = (EditText) findViewById(R.id.txtDescriere);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDrug();
                Toast.makeText(this, "Medicamentul a fost salvat", Toast.LENGTH_LONG).show();
                clean();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDrug(){
        String nume = txtNume.getText().toString();
        String scop = txtScop.getText().toString();
        String unitate = txtUnitate.getText().toString();
        String descriere = txtDescriere.getText().toString();
        Drug drug =  new Drug(nume, scop, unitate, descriere);
        mDatabaseReference.push().setValue(drug);
    }

    private void clean() {
        txtNume.setText("");
        txtScop.setText("");
        txtUnitate.setText("");
        txtDescriere.setText("");
    }
}

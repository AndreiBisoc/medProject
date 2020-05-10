package com.example.medproject.PatientWorkflow.MyDrugs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.Adapters.DrugAdapter;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.example.medproject.Authentication.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MyDrugs extends AppCompatActivity implements View.OnClickListener  {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drugs);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        boolean loggedAsDoctor = intent.getBooleanExtra("loggedAsDoctor", false);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, loggedAsDoctor);


        boolean canEditMedicationFlag = intent.getBooleanExtra("canEditMedicationFlag", false);
        DrugAdapter adapter = new DrugAdapter(intent.getStringExtra("MedicationID"), canEditMedicationFlag, loggedAsDoctor);
        String diagnostic = intent.getStringExtra("diagnostic");
        setTitle(diagnostic);

        RecyclerView rvDrugs = findViewById(R.id.rvDrugs);
        rvDrugs.setAdapter(adapter);

        LinearLayoutManager drugsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDrugs.setLayoutManager(drugsLayoutManager);
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
            startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}

package com.example.medproject.DrugInteraction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.Models.Drug;
import com.example.medproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DrugInteraction extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText drug1, drug2, interactionText, effectText;
    private TextInputLayout interactionLayout, effectLayout;
    private String drug1Name, drug2Name;
    private Button searchInteractionButton;
    private TextView displayDrunkBankInfo;
    private LinearLayout displayInteraction;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_interaction);
        setTitle("Studiază interacțiunile");

        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, true);

        drug1 = findViewById(R.id.drug1);
        drug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.removeAllViews();
            }
        });
        drug2 = findViewById(R.id.drug2);
        searchInteractionButton = findViewById(R.id.searchInteraction);
        searchInteractionButton.setOnClickListener(this);
        displayDrunkBankInfo = findViewById(R.id.displayDrunkBankInfo);
        displayInteraction = findViewById(R.id.displayInteraction);

        interactionLayout = findViewById(R.id.interactionLayout);
        interactionText = findViewById(R.id.interactionText);
        effectLayout = findViewById(R.id.effectLayout);
        effectText = findViewById(R.id.effectText);

    }

    @Override
    public void onClick(View v) {
        drug1Name = drug1.getText().toString();
        drug2Name = drug2.getText().toString();
        drug1.clearFocus();
        drug2.clearFocus();
        int interactionScore = 0, colorId = getInteractionColor(interactionScore);
        ColorStateList color = getColorStateList(colorId);
        BasicActions.manageNavigationView(this, bottomNavigationView, true);
        if(validForm()) {

            checkInteraction();

            displayDrunkBankInfo.setVisibility(View.GONE);
            displayInteraction.setVisibility(View.VISIBLE);
            searchInteractionButton.setText("Caută din nou");
            interactionText.setText(drug1Name + " potențează efectul " + drug2Name);
            interactionLayout.setEndIconDrawable(R.drawable.ic_check_24dp);
            interactionLayout.setEndIconTintList(color);
            effectText.setText(R.string.effect_text);

        }

    }

    private void checkInteraction() {
        // still hardcoded - nothing finished
        drug1Name = "Aspirină";
        DatabaseReference drugsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Drugs");
        String doctorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Drug[] drug1 = new Drug[1];
        final Drug drug2;
        drugsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Drug drug = snapshot.getValue(Drug.class);
                    if (drug.getNume().equals(drug1Name)) {
                        drug1[0] = drug;
                        System.out.println(drug1[0].getDescriere());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private int getInteractionColor(int interactionScore) {
        if(interactionScore >= 0) {
            return R.color.forestgreen;
        } else {
            return R.color.red;
        }
    }

    private boolean validForm() {

        if(drug1Name.isEmpty()) {
            drug1.setError(getString(R.string.set_drug_name));
            drug1.requestFocus();
            return false;
        }

        if(drug2Name.isEmpty()) {
            drug2.setError(getString(R.string.set_drug_name));
            drug2.requestFocus();
            return false;
        }

        return true;
    }
}

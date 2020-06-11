package com.example.medproject.DoctorWorkflow.AddMedication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.DrugInteraction.DrugInteractionHelper;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.FirebaseUtil;
import com.example.medproject.Models.Doctor;
import com.example.medproject.Models.Drug;
import com.example.medproject.Models.DrugAdministration;
import com.example.medproject.Models.MedicationLink;
import com.example.medproject.QRCode.MedicationQRCode.GenerateMedicationQRCode;
import com.example.medproject.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddDrugToMedication extends AppCompatActivity implements View.OnClickListener {
    private EditText searchDrugName, txtDosage, txtNoOfDays, txtStartDay, txtStartHour;
    private TextView noOfInsertedDrugs;
    private MaterialButtonToggleGroup NoOfTimes;

    private final HashMap<String, Drug> drugs = new HashMap<>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private MedicationLink medicationLink = new MedicationLink();
    private final ArrayList<MedicationLink> medicationLinkList = new ArrayList<>();
    private DrugAdministration drugAdministration = new DrugAdministration();
    private final ArrayList<DrugAdministration> drugAdministrationList = new ArrayList<>();
    private final ArrayList<String> medicationDrugIDs = new ArrayList<>();
    private String diagnostic;
    private String drugName;
    private String doctorName;
    private String doctorSpecialization;
    private static int noOfDrugs = 0;
    private ProgressBar progressBar;
    private final Locale locale = Locale.forLanguageTag("ro_RO");
    private TextInputLayout medicineNameInputLayout, NoOfTimesInputLayout;
    private String drugList="";

    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.add_drug_to_medication);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        NoOfTimesInputLayout = findViewById(R.id.NoOfTimesInputLayout);
        medicineNameInputLayout = findViewById(R.id.medicineNameInputLayout);
        progressBar = findViewById(R.id.progressBar);
        searchDrugName = findViewById(R.id.searchDrug);
        txtDosage = findViewById(R.id.txtDosage);
        txtNoOfDays = findViewById(R.id.txtNoOfDays);
        NoOfTimes = findViewById(R.id.NoOfTimes);
        txtStartDay = findViewById(R.id.txtStartDay);
        txtStartDay.setInputType(InputType.TYPE_NULL);
        txtStartHour = findViewById(R.id.txtStartHour);
        txtStartHour.setInputType(InputType.TYPE_NULL);
        Button addAnotherDrugButton = findViewById(R.id.addDrugButton);
        Button saveMedicationButton = findViewById(R.id.saveMedicationButton);
        Button addDrugDetailsButton = findViewById(R.id.addDrugDetailsButton);
        Button checkDrugInteractionButton = findViewById(R.id.checkDrugInteractionButton);
        noOfInsertedDrugs = findViewById(R.id.noOfInsertedDrugs);
        NoOfTimes.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            NoOfTimesInputLayout.setError(null);
        });
        progressBar.setVisibility(View.GONE);
        disableControllers(false);

        if (noOfDrugs == 0) {
            noOfInsertedDrugs.setText(String.format(locale, "%s", "Niciun medicament asociat"));
        } else {
            noOfInsertedDrugs.setText(String.format(locale, "%s", "Aveți adăugate " + noOfDrugs + " medicamente"));
        }

        addAnotherDrugButton.setOnClickListener(this);
        saveMedicationButton.setOnClickListener(this);
        addDrugDetailsButton.setOnClickListener(this);
        checkDrugInteractionButton.setOnClickListener(this);
        txtStartDay.setOnClickListener(this);
        txtStartHour.setOnClickListener(this);

        Intent newDrugIntent = getIntent();
        String drugToAdd = newDrugIntent.getStringExtra("drugId");
        if (drugToAdd != null) {
            finishAddingDrug(drugToAdd);
        }


        Intent intent = getIntent();
        diagnostic = intent.getStringExtra("diagnostic");
        setTitle(diagnostic);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("Drugs")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Drug drug = dataSnapshot.getValue(Drug.class);
                        drug.setId(dataSnapshot.getKey());
                        drugs.put(drug.getNume(), drug);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //get Doctor's name
        String doctorID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = mFirebaseDatabase.getReference("Doctors/" + doctorID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                doctorName = doctor.getLastName() + " " + doctor.getFirstName();
                doctorSpecialization = doctor.getSpecialization();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayInteraction(String interactingDrug) {
        String introducedDrugName = searchDrugName.getText().toString();

//        new android.os.Handler().postDelayed(() -> {
            boolean existsEffectOnHumanBody = interactingDrug != "";
            int iconId = existsEffectOnHumanBody == true ? R.drawable.ic_warning_24dp : R.drawable.ic_check_24dp;
            ColorStateList color = existsEffectOnHumanBody == true ? getColorStateList(R.color.red) : getColorStateList(R.color.forestgreen);
            medicineNameInputLayout.setErrorIconDrawable(iconId);
            if (existsEffectOnHumanBody) {
                medicineNameInputLayout.setError(introducedDrugName + " nu este recomandat cu " + interactingDrug);
            } else {
                medicineNameInputLayout.setError(introducedDrugName + " nu interacționează cu niciunul din medicamentele administrate acestui pacient.");
            }
            medicineNameInputLayout.setErrorIconTintList(color);
            medicineNameInputLayout.setErrorTextColor(color);
            medicineNameInputLayout.setBoxStrokeErrorColor(color);
            medicineNameInputLayout.setHintTextColor(color);
//        },1700);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDrugButton:
                addDrugToMedication();
                break;
            case R.id.saveMedicationButton:
                saveMedication();
                break;
            case R.id.addDrugDetailsButton:
                getSpeechInput();
                break;
            case R.id.txtStartDay:
                openDatePicker();
                break;
            case R.id.txtStartHour:
                openTimePicker();
                break;
            case R.id.checkDrugInteractionButton:
                medicineNameInputLayout.setErrorTextColor(getColorStateList(R.color.amber));
                medicineNameInputLayout.setErrorEnabled(false);
                medicineNameInputLayout.setHintTextColor(getColorStateList(R.color.amber));
                String patientId = getIntent().getStringExtra("patientId");
                final StringBuilder allDrugs = new StringBuilder();
                DatabaseReference patientToMedicationsDBRef = FirebaseDatabase.getInstance().getReference().child("PatientToMedications").child(patientId);
                patientToMedicationsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot medication : dataSnapshot.getChildren()){
                            allDrugs.append(medication.child("drugList").getValue().toString());
                        }
                        String allPatientDrugs = allDrugs.toString();
                        String[] medicineName = new String[1];
                        medicineName[0] = "";
                        boolean[] hasShownMessage = {false};
                        FirebaseUtil.openFbReference("Drugs");
                        DatabaseReference drugsDatabaseReference = FirebaseUtil.mDatabaseReference;
                        drugsDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Drug drug = snapshot.getValue(Drug.class);
                                    if(drug.getNume().equals(searchDrugName.getText().toString())) {
                                        if (drug.getDrugsWithHumanBodyEffect() != null) {
                                            List<String> drugsAdministrated = DrugInteractionHelper.convertToList(allPatientDrugs.split(","));
                                            List<String> interactingDrugs = DrugInteractionHelper.convertToList(drug.getDrugsWithHumanBodyEffect());
                                            for(String interactingDrug : interactingDrugs) {
                                                if(drugsAdministrated.contains(interactingDrug)) {
                                                    medicineName[0]=interactingDrug;
                                                    break;
                                                }
                                            }
                                        }
                                        String introducedDrugName = searchDrugName.getText().toString();

                                        boolean existsEffectOnHumanBody = medicineName[0] != "";
                                        int iconId = existsEffectOnHumanBody == true ? R.drawable.ic_warning_24dp : R.drawable.ic_check_24dp;
                                        ColorStateList color = existsEffectOnHumanBody == true ? getColorStateList(R.color.red) : getColorStateList(R.color.forestgreen);
                                        medicineNameInputLayout.setErrorIconDrawable(iconId);
                                        if (existsEffectOnHumanBody) {
                                            medicineNameInputLayout.setError(introducedDrugName + " nu este recomandat cu " + medicineName[0]);
                                        } else {
                                            medicineNameInputLayout.setError(introducedDrugName + " nu interacționează cu niciunul din medicamentele administrate acestui pacient.");
                                        }
                                        hasShownMessage[0] = true;
                                        medicineNameInputLayout.setErrorIconTintList(color);
                                        medicineNameInputLayout.setErrorTextColor(color);
                                        medicineNameInputLayout.setBoxStrokeErrorColor(color);
                                        medicineNameInputLayout.setHintTextColor(color);
                                    }
                                }
                                if(!hasShownMessage[0]) {
                                    searchDrugName.setError(getString(R.string.not_found_drug));
                                    searchDrugName.requestFocus();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                String interactingDrug = DrugInteractionHelper.getMedicineWhoseCombinationAffectsHumanBody(searchDrugName.getText().toString(), "Paracetamol");
//                new android.os.Handler().postDelayed(() -> {
//                    displayInteraction(interactingDrug);
//                    }, 5000);
                break;
            default:
                break;
        }
    }

    private void saveMedication() {
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        // in cazul adaugarii unei medicatii cu un medicament recent adaugat, medicatia va contine doar numele doctorului in BD
        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        for (DrugAdministration drugAdms : drugAdministrationList) {
            mDatabaseReference.child(drugAdms.getID()).setValue(drugAdms);
        }

        String medicationLinkId = mDatabaseReference.push().getKey();
        mDatabaseReference = mFirebaseDatabase.getReference("Medications/" + medicationLinkId);
        for (int i = 0; i < medicationLinkList.size(); i++) {
            mDatabaseReference.child(medicationDrugIDs.get(i)).setValue(medicationLinkList.get(i));
        }
        mDatabaseReference.child("diagnostic").setValue(diagnostic);
        mDatabaseReference.child("doctorName").setValue(doctorName);
        mDatabaseReference.child("doctorId").setValue(FirebaseAuth.getInstance().getUid());
        mDatabaseReference.child("doctorSpecialization").setValue(doctorSpecialization);
        mDatabaseReference.child("drugList").setValue(drugList);

        finish();
        getDataForQRCode(medicationLinkId);
    }

    private void getDataForQRCode(String medicationLinkId) {
        Intent intentToGenerateQR = new Intent(this, GenerateMedicationQRCode.class);
        intentToGenerateQR.putExtra("medicationId", medicationLinkId);

        Intent intentFromAddMedication = getIntent();
        String patientId = intentFromAddMedication.getStringExtra("patientId");
        intentToGenerateQR.putExtra("patientId", patientId);

        startActivity(intentToGenerateQR);
    }

    private void addDrugToMedication() {
        drugName = searchDrugName.getText().toString().trim();
        drugList += drugName + ",";

        mDatabaseReference = mFirebaseDatabase.getReference("DrugAdministration");
        drugAdministration = new DrugAdministration();
        medicationLink = new MedicationLink();
        drugAdministration.setID(mDatabaseReference.push().getKey());
        String dosageAndUnit = txtDosage.getText().toString().trim();
        if (drugs.get(drugName) != null)
            dosageAndUnit = dosageAndUnit + " " + drugs.get(drugName).getUnitate();
        drugAdministration.setDosage(dosageAndUnit);
        drugAdministration.setNoOfDays(txtNoOfDays.getText().toString().trim());
        drugAdministration.setStartDay(txtStartDay.getText().toString().trim());
        drugAdministration.setStartHour(txtStartHour.getText().toString().trim());
        drugAdministration.setNoOfTimes(String.format(locale, "%d", NoOfTimes.getCheckedButtonIds().size()));

        if (validareDrugAdministration(drugAdministration)) {
            return;
        }

        drugAdministrationList.add(drugAdministration);
        try {
            String drugID = drugs.get(drugName).getId();
            if (medicationDrugIDs.contains(drugID)) {
                medicineNameInputLayout.setErrorIconDrawable(View.AUTOFILL_TYPE_NONE);
                searchDrugName.setError(getString(R.string.already_added_drug));
                searchDrugName.requestFocus();
            } else {
                finishAddingDrug(drugID);
            }

        } catch (Exception e) {
            medicineNameInputLayout.setErrorIconDrawable(View.AUTOFILL_TYPE_NONE);
            searchDrugName.setError(getString(R.string.not_found_drug));
            searchDrugName.requestFocus();
        }
    }

    private void finishAddingDrug(String drugToAdd) {
        medicationDrugIDs.add(drugToAdd);
        medicationLink.setDrugName(drugName);
        medicationLink.setDrugAdministration(drugAdministration.getID());
        medicationLinkList.add(medicationLink);

        clean();
        BasicActions.displaySnackBar(getWindow().getDecorView(), "Ați adăugat " + drugName);
        noOfDrugs++;
        noOfInsertedDrugs.setText(String.format(locale, "%s", "Ați adăugat până acum " + noOfDrugs + " medicamente"));
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String d = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    String m = monthOfYear < 10 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
                    txtStartDay.setText(String.format(locale, "%s/%s/%s", d, m, year1));
                }, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.setTitle("Selectați data de început");
        datePickerDialog.show();
    }

    private void openTimePicker() {
        // Time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK,
                (view, hour, minute) -> {
                    String h = hour < 10 ? "0" + hour : "" + hour;
                    String m = minute < 10 ? "0" + minute : "" + minute;
                    txtStartHour.setText(String.format("%s:%s", h, m));
                }, 8, 0, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setTitle("Selectați ora de început");
        timePickerDialog.show();
    }

    // Speech Recognition -->
    private void getSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro_RO");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ex: Paracetamol, câte 3 comprimate, 10 zile");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 13);
        } else {
            BasicActions.displaySnackBar(getWindow().getDecorView(), "Speech recognition is not supported by your device");
        }
    }

    private int checkNumberOfIntegers(String string) {
        int count = 0;
        for (int i = 1; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i)) && string.charAt(i - 1) == ' ')
                count++;
        }
        return count;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 13) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> resultArray = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // array of possible versions of text
                int nr1 = checkNumberOfIntegers(resultArray.get(0)); //  First 2 versions are the most precise ones
                int nr2 = checkNumberOfIntegers(resultArray.get(1));
                String result = nr1 >= nr2 ? resultArray.get(0) : resultArray.get(1);
                String formatedResult = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
                handleDosage_SpeechRecognition(formatedResult);
                handleDrugName_SpeechRecognition(formatedResult);
                handleNoOfDays_SpeechRecognition(formatedResult);
            }
        }
    }

    private void handleDosage_SpeechRecognition(String result) {
        int indexOfCate = result.indexOf("câte ");
        boolean cate2Exist = result.substring(indexOfCate + 4, result.length() - 1).contains("câte");
        if (indexOfCate == -1 || cate2Exist) { // Cuvantul "câte " nu a fost rostit
            BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
        } else {
            char dosage = result.charAt(indexOfCate + 5);
            if (dosage == 'u') { // ..un comprimat/ml
                txtDosage.setText("1");
            } else if (dosage == 'd') { // ..două comprimate/ml
                txtDosage.setText("2");
            } else { // 3+ comprimate/ml
                int indexOfMl = result.indexOf("ml ");
                if (indexOfMl == -1)
                    txtDosage.setText(String.format("%c", dosage));
                else
                    txtDosage.setText(String.format("%s", result.substring(indexOfCate + 5, indexOfMl - 1)));
            }
        }
    }

    private void handleDrugName_SpeechRecognition(String result) {
        int indexOfCate = result.indexOf("câte ");
        boolean cate2Exist = result.substring(indexOfCate + 4, result.length() - 1).contains("câte");
        if (indexOfCate == -1 || cate2Exist) { // Cuvantul "câte " nu a fost rostit
            BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
        } else {
            String drugName = result.substring(0, indexOfCate - 1);
            searchDrugName.setText(String.format("%s", drugName));
        }
    }

    private void handleNoOfDays_SpeechRecognition(String result) {
        int indexOfComprimate = result.indexOf("comprimate ");
        int indexOfComprimat = result.indexOf("comprimat ");
        int indexOfMl = result.indexOf("ml ");
        boolean comprimate2Exist = result.substring(indexOfComprimate + 11, result.length() - 1).contains("comprimate");
        if ((indexOfComprimat == -1 && indexOfComprimate == -1 && indexOfMl == -1) || comprimate2Exist) { // Niciun cuvant cheie nu a fost rostit
            BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
        } else {
            String str;
            if (indexOfMl == -1)
                str = indexOfComprimat == -1 ? result.substring(indexOfComprimate + 11) : result.substring(indexOfComprimat + 10);
            else
                str = result.substring(indexOfMl + 3);
            String number = str.replaceAll("[^0-9]", "");
            int nr = -1;
            if (number.equals("")) { // o sau două
                if (str.contains("o ")) {
                    nr = 1;
                } else if (str.contains("două ")) {
                    nr = 2;
                } else {
                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
                }
            } else { // 3+
                nr = Integer.parseInt(number);
            }

            if (nr == -1) {
                BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
            } else {
                if (str.contains("zi") || str.contains("zile")) {
                    txtNoOfDays.setText(String.format(locale, "%d", nr));
                } else if (str.contains("săptămână") || str.contains("săptămâni")) {
                    txtNoOfDays.setText(String.format(locale, "%d", nr * 7));
                } else if (str.contains("lună") || str.contains("luni")) {
                    txtNoOfDays.setText(String.format(locale, "%d", nr * 30));
                } else if (str.contains("an") || str.contains("ani")) {
                    txtNoOfDays.setText(String.format(locale, "%d", nr * 365));
                } else {
                    BasicActions.displaySnackBar(getWindow().getDecorView(), "Vă rugăm păstrați tiparul din exemplu");
                }
            }
        }
    }
// Speech Recognition <--

    private void clean() {
        searchDrugName.setText("");
        ColorStateList color = getColorStateList(R.color.amber);
        medicineNameInputLayout.setBoxStrokeErrorColor(color);
        medicineNameInputLayout.setErrorIconDrawable(View.AUTOFILL_TYPE_NONE);
        medicineNameInputLayout.setErrorIconTintList(color);
        medicineNameInputLayout.setErrorEnabled(false);
        medicineNameInputLayout.setHintTextColor(color);
        medicineNameInputLayout.setBoxStrokeErrorColor(color);

        txtDosage.setText("");
        txtNoOfDays.setText("");
        txtStartDay.setText("");
        txtStartHour.setText("");

        NoOfTimes.clearChecked();

        searchDrugName.clearFocus();
        txtDosage.clearFocus();
        txtNoOfDays.clearFocus();
        txtStartDay.clearFocus();
        txtStartHour.clearFocus();

    }

    private boolean validareDrugAdministration(DrugAdministration drugAdministration) {
        if (drugAdministration.getDosage().isEmpty()) {
            txtDosage.setError("Introduceți dozaj");
            txtDosage.requestFocus();
            return true;
        }

        if (drugAdministration.getNoOfTimes().equals("0")) {
            NoOfTimesInputLayout.setError("Selectați perioada administrării");
            return true;
        }

        if (drugAdministration.getNoOfDays().isEmpty()) {
            txtNoOfDays.setError("Introduceți nr. de zile");
            txtNoOfDays.requestFocus();
            return true;
        }

        if (drugAdministration.getStartDay().isEmpty()) {
            txtStartDay.setError("Introduceți data");
            txtStartDay.requestFocus();
            return true;
        }

        if (drugAdministration.getStartHour().isEmpty()) {
            txtStartHour.setError("Introduceți ora");
            txtStartHour.requestFocus();
            return true;
        }
        return false;
    }

    private void disableControllers(boolean shouldBeDisabled) {
        searchDrugName.setEnabled(!shouldBeDisabled);
        txtDosage.setEnabled(!shouldBeDisabled);
        txtNoOfDays.setEnabled(!shouldBeDisabled);
        NoOfTimes.setEnabled(!shouldBeDisabled);
        txtStartDay.setEnabled(!shouldBeDisabled);
        txtStartHour.setEnabled(!shouldBeDisabled);
    }
}

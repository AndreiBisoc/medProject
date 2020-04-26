package com.example.medproject.DoctorWorkflow;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medproject.BasicActions;
import com.example.medproject.R;
import com.example.medproject.data.model.UploadedImage;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AccountDetails_Fragment extends Fragment implements View.OnClickListener {
    private EditText txtCurrentPassword, txtNewPassword, txtVerifyPassword;
    private TextView verified_email;
    private ProgressBar progressBar;
    private Button saveChangesButton, sendVerifyMailButton, chooseIcon;
    private CircleImageView userIcon;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private boolean hasPasswordChanged = false;
    private boolean hasIconChanged = false;
    private Uri imageUri;
    private FirebaseUser user;
    private boolean loggedAsDoctor;

    public AccountDetails_Fragment() {// Required empty public constructor
    }

    public AccountDetails_Fragment(boolean loggedAsDoctor) {
        this.loggedAsDoctor = loggedAsDoctor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_details, container, false);

        txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        txtNewPassword = view.findViewById(R.id.txtNewPassword);
        txtVerifyPassword = view.findViewById(R.id.txtVerifyPassword);
        verified_email = view.findViewById(R.id.verified_email);
        saveChangesButton = view.findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(this);
        sendVerifyMailButton = view.findViewById(R.id.sendVerifyMailButton);
        sendVerifyMailButton.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBar);
        userIcon = view.findViewById(R.id.user_icon);
        chooseIcon = view.findViewById(R.id.chooseImageButton);
        chooseIcon.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference("icons");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = loggedAsDoctor ? databaseReference.child("Doctors") : databaseReference.child("Patients");
        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child(user.getUid())
                .child("image")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UploadedImage uploadedImage = dataSnapshot.getValue(UploadedImage.class);
                        if (uploadedImage != null) {
                            Picasso.get()
                                    .load(uploadedImage.getImageUrl())
                                    .into(userIcon);
                        } else {
                            userIcon.setBackgroundResource(R.drawable.icon_male);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Imaginea nu a putut fi încărcată");
                    }
                });

        int max_length = 35;
        txtCurrentPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max_length)});
        txtNewPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max_length)});
        txtVerifyPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max_length)});
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(getView().findViewById(R.id.container), (AppCompatActivity) getActivity());

        LoadEmailInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveChangesButton:
                saveChanges();
                break;
            case R.id.sendVerifyMailButton:
                sendVerificationMail();
                break;
            case R.id.chooseImageButton:
                choseImage();
                break;
        }
    }

    private void choseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

        hasIconChanged = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            userIcon.setImageURI(imageUri);
        }
    }

    private void LoadEmailInfo() {
        user.reload();
        if (user.isEmailVerified()) {
            verified_email.setVisibility(View.GONE);
            sendVerifyMailButton.setVisibility(View.GONE);
        }
    }

    private void sendVerificationMail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "E-mail de verificare a fost trimis");
            else
                BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "E-mail de verificare nu a putut fi trimis");
        });
    }

    private void saveChanges() {
        final String currentPassword = txtCurrentPassword.getText().toString().trim();
        final String newPassword = txtNewPassword.getText().toString().trim();
        final String verifyPassword = txtVerifyPassword.getText().toString().trim();

        if (validatePassword(currentPassword, newPassword, verifyPassword)) {
            return;
        }

        if (hasPasswordChanged) {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);
            AuthCredential credential = EmailAuthProvider.getCredential(
                    user.getEmail(),
                    currentPassword
            );
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()) {
                            BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Parola a fost schimbată cu succes");
                        } else {
                            BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Parola nu a putut fi schimbată");
                        }
                        progressBar.setVisibility(View.GONE);
                        disableControllers(false);
                        clean();
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    disableControllers(false);

                    txtCurrentPassword.setError("Parolă greșită");
                    txtCurrentPassword.requestFocus();
                }
            });
        }
        if (hasIconChanged) {
            if (imageUri != null) {
                String imageName = user.getUid() + "." + getFileExtension(imageUri);
                StorageReference fileReference = storageReference.child(imageName);
                fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        UploadedImage uploadedImage = new UploadedImage(imageName, uri.toString());
                        databaseReference.child(user.getUid()).child("image").setValue(uploadedImage);
                        BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Imaginea a fost încărcată cu succes");
                    });
                }).addOnFailureListener(e -> {
                    BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Imaginea nu a putut fi încărcată");
                });
            } else {
                BasicActions.displaySnackBar(getActivity().getWindow().getDecorView(), "Nicio imagine nu a fost încărcată");
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean validatePassword(String currentPassword, String newPassword, String verifyPassword) {
        if (currentPassword.isEmpty() && newPassword.isEmpty() && verifyPassword.isEmpty()) {
            hasPasswordChanged = false;
            return false;
        }

        if (currentPassword.isEmpty()) {
            txtCurrentPassword.setError("Introduceți parola curentă");
            txtCurrentPassword.requestFocus();
            return true;
        }

        if (newPassword.isEmpty()) {
            txtNewPassword.setError("Introduceți parola nouă");
            txtNewPassword.requestFocus();
            return true;
        }

        if (verifyPassword.isEmpty()) {
            txtVerifyPassword.setError("Introduceți parola nouă din nou");
            txtVerifyPassword.requestFocus();
            return true;
        }

        if (newPassword.length() < 6) {
            txtNewPassword.setError("Introduceți minim 6 caractere");
            txtNewPassword.requestFocus();
            return true;
        }

        if (!newPassword.equals(verifyPassword)) {
            txtVerifyPassword.setError("Parolele nu corespund");
            txtVerifyPassword.requestFocus();
            return true;
        }

        hasPasswordChanged = true;
        return false;
    }

    private void disableControllers(boolean disable) {
        txtVerifyPassword.setEnabled(!disable);
        txtNewPassword.setEnabled(!disable);
        txtCurrentPassword.setEnabled(!disable);
        saveChangesButton.setEnabled(!disable);
    }

    private void clean() {
        txtCurrentPassword.clearFocus();
        txtNewPassword.clearFocus();
        txtVerifyPassword.clearFocus();

        txtCurrentPassword.setError(null);
        txtNewPassword.setError(null);
        txtVerifyPassword.setError(null);

        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtVerifyPassword.setText("");
    }
}

package com.example.medproject.Models;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.medproject.Models.Exceptions.NotLoggedAsPatientException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Patient implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String phone;
    private String address;
    private String cnp;
    private String bloodType;
    private String RH;
    private String allergies;
    private Contact emergencyContact;
    private String gender;
    private UploadedImage image;

    public Patient() {
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String cnp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.cnp = cnp;
    }

    public Patient(String email, String firstName, String lastName, String birthDate, String phone, String address, String cnp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.cnp = cnp;
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String address, String cnp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.cnp = cnp;
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String address, String cnp, UploadedImage image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.cnp = cnp;
        this.image = image;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Patient)
            return ((Patient) obj).cnp.equals(this.cnp);
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCNP() {
        return cnp;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getRH() {
        return RH;
    }

    public void setRH(String RH) {
        this.RH = RH;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public Contact getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Contact emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UploadedImage getImage() {
        return image;
    }

    public void setImage(UploadedImage image) {
        this.image = image;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getAge(String birthDate) throws NotLoggedAsPatientException {
        if(birthDate == null) {
            throw new NotLoggedAsPatientException();
        }
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate birthday = LocalDate.parse(birthDate, formatter);
        Period p = Period.between(birthday, today);

        return p.getYears();
    }

    public Map<String, Object> setBasicDetails(String firstName, String lastName, String phone, String address, String CNP, String birthDate) {
        return new HashMap<String, Object>(){{
            put("firstName", firstName);
            put("lastName", lastName);
            put("name", firstName + " " + lastName);
            put("phone", phone);
            put("address", address);
            put("CNP", CNP);
            put("birthDate", birthDate);
        }};
    }

    public Map<String, Object> setEmergencyDetails(String gender, String bloodType, String RHType, String allergies, Contact emergencyContact) {
        return new HashMap<String, Object>() {{
            put("gender", gender);
            put("bloodType", bloodType);
            put("RH", RHType);
            put("allergies", allergies);
            put("emergencyContact", emergencyContact);
        }};
    }
}

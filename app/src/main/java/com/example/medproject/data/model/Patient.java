package com.example.medproject.data.model;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Patient implements Serializable {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String phone;
    private String address;
    private String CNP;
    private String bloodType;
    private String RH;
    private String allergies;
    private Contact emergencyContact;
    private String gender;

    public Patient() {
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String CNP) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.CNP = CNP;
    }

    public Patient(String email, String firstName, String lastName, String birthDate, String phone, String address, String CNP) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.CNP = CNP;
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String address, String CNP) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.CNP = CNP;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Patient)
            return ((Patient) obj).CNP == this.CNP;
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
        return CNP;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getAge(String birthDate) throws  NotLoggedAsPatientException{
        if(birthDate == null) {
            throw new NotLoggedAsPatientException();
        }
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate birthday = LocalDate.parse(birthDate, formatter);
        Period p = Period.between(birthday, today);

        return p.getYears();
    }

    public Map<String, Object> setEmergencyDetails(String gender, String bloodType, String RHType, String allergies, Contact emergencyContact) {
        Map<String, Object> emergencyDetails = new HashMap<String, Object>();
        emergencyDetails.put("gender", gender);
        emergencyDetails.put("bloodType", bloodType);
        emergencyDetails.put("RH", RHType);
        emergencyDetails.put("allergies", allergies);
        emergencyDetails.put("emergencyContact", emergencyContact);
//        this.allergies = Arrays.asList(allergies.split("\\s*,\\s*"));
        return emergencyDetails;
    }

}

package com.example.medproject.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Doctor {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String adresaCabinet;
    private List<Patient> patientList;

    public Doctor() {}

    public Doctor(String firstName, String lastName, String specialization, String phone, String adresaCabinet) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.adresaCabinet = adresaCabinet;
        this.patientList = new ArrayList<>();
    }

    public Doctor(String email, String firstName, String lastName, String specialization, String phone, String adresaCabinet) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.adresaCabinet = adresaCabinet;
        this.patientList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdresaCabinet() {
        return adresaCabinet;
    }

    public void setAdresaCabinet(String adresaCabinet) {
        this.adresaCabinet = adresaCabinet;
    }


    public List<Patient> getPatientList() { return patientList; }

    public void addPatient(Patient patient) { this.patientList.add(patient); }

}

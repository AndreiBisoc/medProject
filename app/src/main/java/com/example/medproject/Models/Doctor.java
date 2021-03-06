package com.example.medproject.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doctor {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String adresaCabinet;
    private List<Patient> patientList;
    private UploadedImage image;

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

    public Doctor(String email, String firstName, String lastName, String specialization, String phone, String adresaCabinet, UploadedImage image) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.phone = phone;
        this.adresaCabinet = adresaCabinet;
        this.patientList = new ArrayList<>();
        this.image = image;
    }

    public String getName() {
        return lastName + " " + firstName;
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

    public UploadedImage getImage() {
        return image;
    }

    public void setImage(UploadedImage image) {
        this.image = image;
    }

    public Map<String, Object> updateDoctorDetails(String firstName, String lastName, String phone, String address, String specialization) {
        return new HashMap<String, Object>() {{
            put("firstName", firstName);
            put("lastName", lastName);
            put("name", firstName + " " + lastName);
            put("phone", phone);
            put("adresaCabinet", address);
            put("specialization", specialization);
        }};
    }
}

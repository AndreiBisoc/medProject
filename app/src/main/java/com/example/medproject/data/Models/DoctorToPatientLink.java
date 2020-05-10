package com.example.medproject.data.Models;

public class DoctorToPatientLink {

    private Patient patient;

    private String date;

    public DoctorToPatientLink(){

    }

    public DoctorToPatientLink(Patient patient, String date) {
        this.patient = patient;
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

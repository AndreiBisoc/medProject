package com.example.medproject.data.Models;

public class MedicationLink {
    private String Id;
    private String diagnostic;
    private String drugName;
    private String doctorName;
    private String drugAdministration;

    public MedicationLink(){}

    public MedicationLink(String Id, String diagnostic, String drugName) {
        this.Id = Id;
        this.diagnostic = diagnostic;
        this.drugName = drugName;
    }

    public MedicationLink(String Id, String diagnostic, String drugName, String doctorName, String drugAdministration) {
        this.Id = Id;
        this.diagnostic = diagnostic;
        this.drugName = drugName;
        this.doctorName = doctorName;
        this.drugAdministration = drugAdministration;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDrugAdministration() {
        return drugAdministration;
    }

    public void setDrugAdministration(String drugAdministration) {
        this.drugAdministration = drugAdministration;
    }
}

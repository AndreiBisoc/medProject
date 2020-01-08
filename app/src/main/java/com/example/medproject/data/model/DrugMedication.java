package com.example.medproject.data.model;

public class DrugMedication {
    private String diagnostic;
    private String doctorName;
    private String drugAdministrationID;
    private String drugName;

    public DrugMedication(){}

    public DrugMedication(String ID, String diagnostic, String doctorName, String drugAdministrationID, String drugName) {
        this.diagnostic = diagnostic;
        this.doctorName = doctorName;
        this.drugAdministrationID = drugAdministrationID;
        this.drugName = drugName;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDrugAdministrationID() {
        return drugAdministrationID;
    }

    public void setDrugAdministrationID(String drugAdministrationID) {
        this.drugAdministrationID = drugAdministrationID;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
}

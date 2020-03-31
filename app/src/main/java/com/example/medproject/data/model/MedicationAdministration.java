package com.example.medproject.data.model;

public class MedicationAdministration {
    private String drugAdministration;
    private String drugName;
    private String drugId;

    public MedicationAdministration(){
    }

    public MedicationAdministration(String drugAdministration, String drugName, String drugId) {
        this.drugAdministration = drugAdministration;
        this.drugName = drugName;
        this.drugId = drugId;
    }

    public String getDrugAdministration() {
        return drugAdministration;
    }

    public void setDrugAdministration(String drugAdministration) {
        this.drugAdministration = drugAdministration;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }
}
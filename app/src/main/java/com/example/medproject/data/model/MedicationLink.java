package com.example.medproject.data.model;

public class MedicationLink {
    private String name;
    private String drugAdministration;


    public MedicationLink(){}

    public MedicationLink(String name, String drugAdministration) {
        this.name = name;
        this.drugAdministration = drugAdministration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrugAdministration() {
        return drugAdministration;
    }

    public void setDrugAdministration(String drugAdministration) {
        this.drugAdministration = drugAdministration;
    }
}

package com.example.medproject.data.model;

public class MedicationLink {
    private String ID;
    private String name;
    private String drugAdministration;


    public MedicationLink(){}

    public MedicationLink(String ID, String name, String drugAdministration) {
        this.ID = ID;
        this.name = name;
        this.drugAdministration = drugAdministration;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

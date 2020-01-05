package com.example.medproject.data.model;

import java.io.Serializable;
import java.util.List;

public class Medication implements Serializable {
    private String id;
    private String diagnostic;
    private String doctorName;
    private List<Drug> drugList;

    public Medication(){}

    public Medication(String id, String diagnostic, String doctorName, List<Drug> drugList) {
        this.id = id;
        this.diagnostic = diagnostic;
        this.doctorName = doctorName;
        this.drugList = drugList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Drug> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<Drug> drugList) {
        this.drugList = drugList;
    }
}

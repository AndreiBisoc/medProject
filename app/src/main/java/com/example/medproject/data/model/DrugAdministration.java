package com.example.medproject.data.model;

import java.io.Serializable;

public class DrugAdministration implements Serializable {
    private String ID;
    private String dosage;
    private String noOfDays;
    private String noOfTimes;
    private String startDay;
    private String startHour;

    public DrugAdministration(){}

    public DrugAdministration(String ID, String dosage, String noOfDays, String noOfTimes, String startDay, String startHour) {
        this.ID = ID;
        this.dosage = dosage;
        this.noOfDays = noOfDays;
        this.noOfTimes = noOfTimes;
        this.startDay = startDay;
        this.startHour = startHour;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public String getNoOfTimes() {
        return noOfTimes;
    }

    public void setNoOfTimes(String noOfTimes) {
        this.noOfTimes = noOfTimes;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }
}

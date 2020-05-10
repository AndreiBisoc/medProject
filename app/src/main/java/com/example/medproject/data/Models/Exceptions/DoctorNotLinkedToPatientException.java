package com.example.medproject.data.Models.Exceptions;

public class DoctorNotLinkedToPatientException extends RuntimeException {

    public String toString(){
        return "You are not a patient of this doctor!";
    }
}

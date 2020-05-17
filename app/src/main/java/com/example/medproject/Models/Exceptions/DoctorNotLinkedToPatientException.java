package com.example.medproject.Models.Exceptions;

public class DoctorNotLinkedToPatientException extends RuntimeException {

    public String toString(){
        return "You are not a patient of this doctor!";
    }
}

package com.example.medproject.data.model.Exceptions;

public class DoctorNotLinkedToPatientException extends RuntimeException {

    public String toString(){
        return "You are not a patient of this doctor!";
    }
}

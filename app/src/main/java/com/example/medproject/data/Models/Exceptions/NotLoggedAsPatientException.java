package com.example.medproject.data.Models.Exceptions;

public class NotLoggedAsPatientException extends RuntimeException {

    public String toString(){
        return "You are not logged as a patient!";
    }
}

package com.example.medproject.Models.Exceptions;

public class NotLoggedAsPatientException extends RuntimeException {

    public String toString(){
        return "Nu sunteți logat cu contul unui pacient!";
    }
}

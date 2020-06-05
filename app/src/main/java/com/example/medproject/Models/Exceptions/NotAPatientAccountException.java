package com.example.medproject.Models.Exceptions;

public class NotAPatientAccountException extends RuntimeException{

    public String toString(){
        return "Codul QR nu conține id-ul unui pacient!";
    }

}

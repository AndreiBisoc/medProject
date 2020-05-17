package com.example.medproject.Models.Exceptions;

public class WrongPatientScanningQRException extends RuntimeException {
    @Override
    public String toString() {
        return "Codul QR nu conține medicația dumneavoastră.";
    }
}

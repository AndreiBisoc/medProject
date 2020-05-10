package com.example.medproject.data.Models.Exceptions;

public class WrongPatientScanningQRException extends RuntimeException {
    @Override
    public String toString() {
        return "Codul QR nu conține medicația dumneavoastră.";
    }
}

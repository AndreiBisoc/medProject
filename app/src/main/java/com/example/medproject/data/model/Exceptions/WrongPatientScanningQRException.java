package com.example.medproject.data.model.Exceptions;

public class WrongPatientScanningQRException extends RuntimeException {
    @Override
    public String toString() {
        return "Codul QR nu conține medicația dumneavoastră.";
    }
}

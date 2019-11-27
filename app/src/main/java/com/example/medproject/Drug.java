package com.example.medproject;

public class Drug {
    private String id;
    private String nume;
    private String scop;
    private String unitate;
    private String descriere;

    public Drug(){}

    public Drug(String nume, String scop, String unitate, String descriere) {
        this.setId(id);
        this.setNume(nume);
        this.setScop(scop);
        this.setUnitate(unitate);
        this.setDescriere(descriere);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String name) {
        this.nume = name;
    }

    public String getScop() {
        return scop;
    }

    public void setScop(String scop) {
        this.scop = scop;
    }

    public String getUnitate() {
        return unitate;
    }

    public void setUnitate(String unitate) {
        this.unitate = unitate;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}

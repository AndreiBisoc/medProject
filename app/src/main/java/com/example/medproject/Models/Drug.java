package com.example.medproject.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Drug implements Serializable {
    private String id;
    private String nume;
    private String scop;
    private String unitate;
    private String descriere;
    private String[] drugsWithHumanBodyEffect;
    private String[] positiveInteractingDrugs;
    private String[] negativeInteractingDrugs;

    public Drug(){}

    public Drug(String nume){
        this.setId(id);
        this.setNume(nume);
    }

    public Drug(String nume, String scop, String unitate, String descriere,String drugsWithHumanBodyEffect,
                String positiveInteractingDrugs, String negativeInteractingDrugs) {
        this.setId(id);
        this.setNume(nume);
        this.setScop(scop);
        this.setUnitate(unitate);
        this.setDescriere(descriere);
        this.setDrugsWithHumanBodyEffect(drugsWithHumanBodyEffect);
        this.setPositiveInteractingDrugs(positiveInteractingDrugs);
        this.setNegativeInteractingDrugs(negativeInteractingDrugs);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Drug)
            return ((Drug) obj).nume.equals(nume);
        return false;
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

    public String[] getDrugsWithHumanBodyEffect() {
        return drugsWithHumanBodyEffect;
    }

    public void setDrugsWithHumanBodyEffect(String drugsWithHumanBodyEffect) {
        this.drugsWithHumanBodyEffect = drugsWithHumanBodyEffect.split(",");
    }

    public String[] getPositiveInteractingDrugs() {
        return positiveInteractingDrugs;
    }

    public void setPositiveInteractingDrugs(String positiveInteractingDrugs) {
        this.positiveInteractingDrugs = positiveInteractingDrugs.split(",");
    }

    public String[] getNegativeInteractingDrugs() {
        return negativeInteractingDrugs;
    }

    public void setNegativeInteractingDrugs(String negativeInteractingDrugs) {
        this.negativeInteractingDrugs = negativeInteractingDrugs.split(",");
    }

}

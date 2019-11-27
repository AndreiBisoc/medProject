package com.example.medproject;

import java.util.Date;

public class Patient {
    private String id;
    private String firstname;
    private String lastname;
    private Date birthDate;
    private String profession;
    private Number phone;
    private String address;
    private String imageUrl;

    public Patient(String id, String firstname, String lastname, String profession, Date birthDate,
                   Number phone, String address, String imageUrl) {
        this.setId(id);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setProfession(profession);
        this.setBirthDate(birthDate);
        this.setPhone(phone);
        this.setAddress(address);
        this.setImageUrl(imageUrl);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Number getPhone() {
        return phone;
    }

    public void setPhone(Number phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

package com.example.medproject;

import java.util.Date;

public class Patient {
    private String id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String profession;
    private Number phone;
    private String address;
    private String imageUrl;

    public Patient(String id, String firstName, String lastname, String profession, Date birthDate,
                   Number phone, String address, String imageUrl) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastname);
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

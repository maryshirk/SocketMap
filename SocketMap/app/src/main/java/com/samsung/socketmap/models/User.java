package com.samsung.socketmap.models;

public class User {
    private String name, email, pass, city, phone;

    public User(String name, String email, String pass, String city) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.city = city;
        this.phone = "не указано";
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getCity() {
        return city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

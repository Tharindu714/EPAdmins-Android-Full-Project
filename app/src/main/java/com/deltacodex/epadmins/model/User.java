package com.deltacodex.epadmins.model;

public class User {
    private String email;
    private String username;
    private String gender;
    private String location_name;
    private String mobile;
    private String status;

    public User() {
    }

    public User(String email, String username, String gender, String location_name, String mobile, String status) {
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.location_name = location_name;
        this.mobile = mobile;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

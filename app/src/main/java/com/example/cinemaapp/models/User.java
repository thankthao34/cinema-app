package com.example.cinemaapp.models;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String avatarUrl;
    public String fcmToken;

    public User() {
    }

    public User(String uid, String name, String email, String phone, String avatarUrl, String fcmToken) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.fcmToken = fcmToken;
    }
}

package com.example.cinemaapp.models;

public class Theater {
    public String theaterId;
    public String name;
    public String address;
    public String city;
    public double latitude;
    public double longitude;
    public String imageUrl;

    public Theater() {
    }

    public Theater(String theaterId, String name, String address, String city,
                   double latitude, double longitude, String imageUrl) {
        this.theaterId = theaterId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }
}

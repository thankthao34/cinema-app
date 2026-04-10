package com.example.cinemaapp.models;

import java.util.Map;

public class Showtime {
    public String showtimeId;
    public String movieId;
    public String theaterId;
    public String roomName;
    public long startTime;
    public long endTime;
    public double price;
    public int totalSeats;
    public int availableSeats;
    public Map<String, String> seatMap;

    public Showtime() {
    }
}

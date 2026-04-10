package com.example.cinemaapp.models;

import java.util.List;

public class Ticket {
    public String ticketId;
    public String userId;
    public String movieId;
    public String showtimeId;
    public String theaterId;
    public List<String> seats;
    public double totalPrice;
    public long bookingTime;
    public String status;
    public String qrCode;
    public boolean notifSent;

    // Derived fields for UI after joining tickets with movies and showtimes.
    public String movieTitle;
    public long showtimeStartTime;
    public String userName;
    public String roomName;

    public Ticket() {
    }
}

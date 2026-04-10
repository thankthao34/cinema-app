package com.example.cinemaapp.models;

import java.util.List;

public class Movie {
    public String movieId;
    public String title;
    public String description;
    public List<String> genre;
    public int duration;
    public float rating;
    public String ageLimit;
    public String posterUrl;
    public String trailerUrl;
    public String language;
    public boolean isNowShowing;
    public boolean isComingSoon;

    public Movie() {
    }

    public Movie(String movieId, String title, String description, List<String> genre, int duration,
                 float rating, String ageLimit, String posterUrl, String trailerUrl, String language,
                 boolean isNowShowing, boolean isComingSoon) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.ageLimit = ageLimit;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.language = language;
        this.isNowShowing = isNowShowing;
        this.isComingSoon = isComingSoon;
    }
}

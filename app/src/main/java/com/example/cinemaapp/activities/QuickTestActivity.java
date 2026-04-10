package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.services.DevDataSeeder;
import com.example.cinemaapp.services.NotificationHelper;
import com.example.cinemaapp.utils.Constants;

import java.util.ArrayList;

public class QuickTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_test);

        NotificationHelper.createChannel(this);

        Button btnSeed = findViewById(R.id.btnSeedData);
        Button btnLogin = findViewById(R.id.btnGoLogin);
        Button btnHome = findViewById(R.id.btnGoHome);
        Button btnMovie = findViewById(R.id.btnGoMovie);
        Button btnShowtime = findViewById(R.id.btnGoShowtime);
        Button btnSeat = findViewById(R.id.btnGoSeat);
        Button btnCheckout = findViewById(R.id.btnGoCheckout);
        Button btnTicket = findViewById(R.id.btnGoTicket);
        Button btnNotify = findViewById(R.id.btnLocalNotify);

        btnSeed.setOnClickListener(v -> {
            DevDataSeeder.seedIfNeeded(this);
            Toast.makeText(this, "Seed requested for dev mode", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnMovie.setOnClickListener(v -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, "m1");
            startActivity(intent);
        });

        btnShowtime.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectShowtimeActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, "m1");
            startActivity(intent);
        });

        btnSeat.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeatSelectionActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, "m1");
            intent.putExtra(Constants.EXTRA_SHOWTIME_ID, "s1");
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            ArrayList<String> seats = new ArrayList<>();
            seats.add("A2");
            seats.add("A3");
            intent.putExtra(Constants.EXTRA_MOVIE_ID, "m1");
            intent.putExtra(Constants.EXTRA_SHOWTIME_ID, "s1");
            intent.putStringArrayListExtra(Constants.EXTRA_SELECTED_SEATS, seats);
            intent.putExtra(Constants.EXTRA_TOTAL_PRICE, 240000d);
            intent.putExtra(Constants.EXTRA_SHOWTIME_START_MS, System.currentTimeMillis() + 3600_000L);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(v -> {
            Intent intent = new Intent(this, TicketDetailActivity.class);
            intent.putExtra(Constants.EXTRA_TICKET_ID, "TEST-TICKET");
            startActivity(intent);
        });

        btnNotify.setOnClickListener(v -> {
            NotificationHelper.showNotification(
                    this,
                    "Test Notification",
                    "Nhan vao de mo e-ticket",
                    "TEST-TICKET"
            );
        });
    }
}

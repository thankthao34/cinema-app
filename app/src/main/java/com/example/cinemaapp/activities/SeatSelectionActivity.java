package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.adapters.SeatAdapter;
import com.example.cinemaapp.models.Showtime;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeatSelectionActivity extends AppCompatActivity {
    private final FirestoreService firestoreService = new FirestoreService();
    private TextView tvSelected;
    private TextView tvTotal;
    private Button btnContinue;
    private SeatAdapter seatAdapter;
    private String showtimeId;
    private String movieId;
    private String theaterId;
    private long showtimeStartMillis;
    private double seatPrice = 110000;
    private final List<String> selectedSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        showtimeId = getIntent().getStringExtra(Constants.EXTRA_SHOWTIME_ID);
        movieId = getIntent().getStringExtra(Constants.EXTRA_MOVIE_ID);

        RecyclerView rvSeats = findViewById(R.id.rvSeats);
        tvSelected = findViewById(R.id.tvSelectedSeats);
        tvTotal = findViewById(R.id.tvTotalPrice);
        btnContinue = findViewById(R.id.btnContinueCheckout);
        btnContinue.setEnabled(false);

        rvSeats.setLayoutManager(new GridLayoutManager(this, 8));
        seatAdapter = new SeatAdapter(this::updateSelection);
        rvSeats.setAdapter(seatAdapter);

        loadShowtimeSeats();

        btnContinue.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui long chon it nhat 1 ghe", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra(Constants.EXTRA_SHOWTIME_ID, showtimeId);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
            intent.putExtra(Constants.EXTRA_THEATER_ID, theaterId);
            intent.putStringArrayListExtra(Constants.EXTRA_SELECTED_SEATS, new ArrayList<>(selectedSeats));
            intent.putExtra(Constants.EXTRA_TOTAL_PRICE, seatPrice * selectedSeats.size());
            intent.putExtra(Constants.EXTRA_SHOWTIME_START_MS, showtimeStartMillis);
            startActivity(intent);
        });
    }

    private void loadShowtimeSeats() {
        if (showtimeId == null || showtimeId.trim().isEmpty()) {
            Toast.makeText(this, "Khong tim thay suat chieu", Toast.LENGTH_SHORT).show();
            seatAdapter.setData(generateDefaultSeats());
            return;
        }

        firestoreService.getShowtimeById(showtimeId)
                .addOnSuccessListener(snapshot -> {
                    Showtime showtime = firestoreService.mapShowtime(snapshot);
                    if (showtime == null) {
                        Toast.makeText(this, "Suat chieu khong ton tai", Toast.LENGTH_SHORT).show();
                        seatAdapter.setData(generateDefaultSeats());
                        return;
                    }

                    theaterId = showtime.theaterId;
                    showtimeStartMillis = showtime.startTime;
                    if (showtime.price > 0d) {
                        seatPrice = showtime.price;
                    }

                    List<String> seats = new ArrayList<>();
                    Set<String> unavailable = new HashSet<>();
                    Map<String, String> seatMap = showtime.seatMap;
                    if (seatMap != null && !seatMap.isEmpty()) {
                        seats.addAll(seatMap.keySet());
                        Collections.sort(seats);
                        for (Map.Entry<String, String> entry : seatMap.entrySet()) {
                            if ("booked".equalsIgnoreCase(entry.getValue())) {
                                unavailable.add(entry.getKey());
                            }
                        }
                    } else {
                        seats = generateDefaultSeats();
                    }

                    seatAdapter.setData(seats);
                    seatAdapter.setUnavailableSeats(unavailable);
                    tvTotal.setText("Tong: 0 VND");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Loi tai ghe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    seatAdapter.setData(generateDefaultSeats());
                });
    }

    private List<String> generateDefaultSeats() {
        List<String> data = new ArrayList<>();
        for (char row = 'A'; row <= 'F'; row++) {
            for (int col = 1; col <= 8; col++) {
                data.add(row + String.valueOf(col));
            }
        }
        return data;
    }

    private void updateSelection(Set<String> selectedSeats) {
        this.selectedSeats.clear();
        this.selectedSeats.addAll(selectedSeats);
        Collections.sort(this.selectedSeats);
        tvSelected.setText("Ban dang chon: " + this.selectedSeats);
        double total = this.selectedSeats.size() * seatPrice;
        tvTotal.setText("Tong: " + String.format("%,.0f", total) + " VND");
        btnContinue.setEnabled(!this.selectedSeats.isEmpty());
    }
}

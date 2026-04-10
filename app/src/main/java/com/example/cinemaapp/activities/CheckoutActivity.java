package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.services.NotificationHelper;
import com.example.cinemaapp.services.NotificationScheduler;
import com.example.cinemaapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private final FirestoreService firestoreService = new FirestoreService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        String showtimeId = getIntent().getStringExtra(Constants.EXTRA_SHOWTIME_ID);
        String movieId = getIntent().getStringExtra(Constants.EXTRA_MOVIE_ID);
        ArrayList<String> selectedSeats = getIntent().getStringArrayListExtra(Constants.EXTRA_SELECTED_SEATS);
        double totalPrice = getIntent().getDoubleExtra(Constants.EXTRA_TOTAL_PRICE, 0d);
        long showtimeStartMillis = getIntent().getLongExtra(Constants.EXTRA_SHOWTIME_START_MS, 0L);

        TextView tvOrderSummary = findViewById(R.id.tvOrderSummary);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);
        ProgressBar pbCheckout = findViewById(R.id.pbCheckout);

        NotificationHelper.createChannel(this);

        int seatCount = selectedSeats == null ? 0 : selectedSeats.size();
        tvOrderSummary.setText("Ghe: " + (selectedSeats == null ? "[]" : selectedSeats.toString()));
        tvTotalAmount.setText("Tong cong: " + String.format(Locale.getDefault(), "%,.0f", totalPrice) + " VND");

        btnConfirm.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Ban can dang nhap", Toast.LENGTH_SHORT).show();
                return;
            }
            if (showtimeId == null || movieId == null || seatCount == 0) {
                Toast.makeText(this, "Thieu du lieu dat ve", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirm.setEnabled(false);
            pbCheckout.setVisibility(View.VISIBLE);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firestoreService.bookSeatsAndCreateTicket(userId, movieId, showtimeId, selectedSeats, totalPrice)
                    .addOnSuccessListener(ticketId -> {
                        NotificationScheduler.scheduleShowtimeReminder(
                                this,
                                ticketId,
                                movieId,
                                showtimeStartMillis
                        );
                        Toast.makeText(this, "Dat ve thanh cong", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, TicketDetailActivity.class);
                        intent.putExtra(Constants.EXTRA_TICKET_ID, ticketId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnConfirm.setEnabled(true);
                        pbCheckout.setVisibility(View.GONE);
                        Toast.makeText(this, "Dat ve that bai: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}

package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.adapters.ShowtimeAdapter;
import com.example.cinemaapp.models.Showtime;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectShowtimeActivity extends AppCompatActivity {
    private final FirestoreService firestoreService = new FirestoreService();
    private String selectedShowtimeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_showtime);

        String movieId = getIntent().getStringExtra(Constants.EXTRA_MOVIE_ID);
        TextView tvHeader = findViewById(R.id.tvShowtimeHeader);
        TextView tvStatus = findViewById(R.id.tvShowtimeStatus);
        Button btnChoose = findViewById(R.id.btnChooseShowtime);
        RecyclerView rvShowtimes = findViewById(R.id.rvShowtimes);

        tvHeader.setText("Suat chieu cho phim: " + (movieId == null ? "N/A" : movieId));
        btnChoose.setEnabled(false);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        ShowtimeAdapter adapter = new ShowtimeAdapter(showtime -> {
            selectedShowtimeId = showtime.showtimeId;
            btnChoose.setEnabled(true);
            tvStatus.setText("Da chon: " + showtime.roomName);
        });
        rvShowtimes.setAdapter(adapter);

        if (movieId == null || movieId.trim().isEmpty()) {
            tvStatus.setText("Khong tim thay movieId");
        } else {
            firestoreService.getShowtimesByMovie(movieId)
                    .addOnSuccessListener(snapshot -> {
                        List<Showtime> showtimes = firestoreService.mapShowtimes(snapshot);
                        Collections.sort(showtimes, Comparator.comparingLong(s -> s.startTime));
                        adapter.setData(showtimes);
                        if (adapter.getItemCount() == 0) {
                            tvStatus.setText("Chua co suat chieu cho phim nay");
                            rvShowtimes.setVisibility(RecyclerView.GONE);
                        } else {
                            rvShowtimes.setVisibility(RecyclerView.VISIBLE);
                            tvStatus.setText("Chon mot suat chieu de tiep tuc");
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvStatus.setText("Loi tai suat chieu");
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        btnChoose.setOnClickListener(v -> {
            if (selectedShowtimeId == null || selectedShowtimeId.isEmpty()) {
                Toast.makeText(this, "Vui long chon suat chieu", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, SeatSelectionActivity.class);
            intent.putExtra(Constants.EXTRA_SHOWTIME_ID, selectedShowtimeId);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
            startActivity(intent);
        });
    }
}

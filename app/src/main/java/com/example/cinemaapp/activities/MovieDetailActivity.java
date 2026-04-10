package com.example.cinemaapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.Constants;
import com.example.cinemaapp.utils.LocalImageResolver;

public class MovieDetailActivity extends AppCompatActivity {
    private final FirestoreService firestoreService = new FirestoreService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        String movieId = getIntent().getStringExtra(Constants.EXTRA_MOVIE_ID);
        ImageView ivPoster = findViewById(R.id.ivMoviePoster);
        TextView tvTitle = findViewById(R.id.tvMovieTitle);
        TextView tvDescription = findViewById(R.id.tvMovieDescription);
        Button btnBook = findViewById(R.id.btnBookNow);

        tvTitle.setText("Dang tai thong tin phim...");
        tvDescription.setText("Vui long cho...");

        if (movieId == null || movieId.trim().isEmpty()) {
            tvTitle.setText("Khong tim thay phim");
            tvDescription.setText("Movie ID khong hop le");
            btnBook.setEnabled(false);
            return;
        }

        firestoreService.getMovieById(movieId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        tvTitle.setText("Phim khong ton tai");
                        tvDescription.setText("Khong co du lieu tren Firestore");
                        btnBook.setEnabled(false);
                        return;
                    }
                    Movie movie = documentSnapshot.toObject(Movie.class);
                    if (movie == null) {
                        tvTitle.setText("Khong doc duoc du lieu phim");
                        tvDescription.setText("Du lieu phim khong hop le");
                        btnBook.setEnabled(false);
                        return;
                    }
                    tvTitle.setText(movie.title == null ? "Movie Detail" : movie.title);
                        ivPoster.setImageResource(LocalImageResolver.resolvePoster(this, movie.posterUrl));
                    tvDescription.setText(movie.description == null || movie.description.isEmpty()
                            ? "Chua co mo ta phim"
                            : movie.description);
                })
                .addOnFailureListener(e -> {
                    tvTitle.setText("Loi tai phim");
                    tvDescription.setText(e.getMessage());
                    btnBook.setEnabled(false);
                    Toast.makeText(this, "Loi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectShowtimeActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movieId);
            startActivity(intent);
        });
    }
}

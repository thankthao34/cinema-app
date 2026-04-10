package com.example.cinemaapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.activities.MovieDetailActivity;
import com.example.cinemaapp.adapters.MovieAdapter;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.Constants;
import com.google.firebase.firestore.ListenerRegistration;

public class HomeFragment extends Fragment {
    private final FirestoreService firestoreService = new FirestoreService();
    private ListenerRegistration moviesRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvStatus = view.findViewById(R.id.tvHomeStatus);
        RecyclerView rvNowShowing = view.findViewById(R.id.rvNowShowing);
        rvNowShowing.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        MovieAdapter adapter = new MovieAdapter(movie -> {
            Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movie.movieId);
            startActivity(intent);
        });

        rvNowShowing.setAdapter(adapter);
        tvStatus.setText("Dang tai du lieu...");
        moviesRegistration = firestoreService.listenNowShowingMovies((value, error) -> {
            if (error != null) {
                tvStatus.setText("Loi tai phim: " + error.getMessage());
                rvNowShowing.setVisibility(View.GONE);
                return;
            }

            adapter.setData(firestoreService.mapMovies(value));
            if (adapter.getItemCount() == 0) {
                tvStatus.setText("Chua co phim dang chieu");
                rvNowShowing.setVisibility(View.GONE);
            } else {
                tvStatus.setVisibility(View.GONE);
                rvNowShowing.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (moviesRegistration != null) {
            moviesRegistration.remove();
            moviesRegistration = null;
        }
    }
}

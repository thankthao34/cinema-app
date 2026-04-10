package com.example.cinemaapp.fragments;

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
import com.example.cinemaapp.adapters.TicketAdapter;
import com.example.cinemaapp.services.FirestoreService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

public class MyTicketsFragment extends Fragment {
    private final FirestoreService firestoreService = new FirestoreService();
    private ListenerRegistration ticketsRegistration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvStatus = view.findViewById(R.id.tvTicketsStatus);
        RecyclerView rvTickets = view.findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        TicketAdapter adapter = new TicketAdapter();
        rvTickets.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            tvStatus.setText("Ban can dang nhap de xem ve");
            rvTickets.setVisibility(View.GONE);
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tvStatus.setText("Dang tai ve...");
        ticketsRegistration = firestoreService.listenTicketsByUser(userId, (value, error) -> {
            if (error != null) {
                tvStatus.setText("Loi tai ve: " + error.getMessage());
                rvTickets.setVisibility(View.GONE);
                return;
            }

            if (value == null || value.isEmpty()) {
                tvStatus.setText("Ban chua co ve nao");
                rvTickets.setVisibility(View.GONE);
                adapter.setData(null);
                return;
            }

            tvStatus.setText("Dang dong bo thong tin phim va suat chieu...");
            firestoreService.enrichTicketsWithMovieAndShowtime(firestoreService.mapTickets(value),
                    new FirestoreService.TicketsEnrichedCallback() {
                        @Override
                        public void onSuccess(java.util.List<com.example.cinemaapp.models.Ticket> tickets) {
                            adapter.setData(tickets);
                            if (adapter.getItemCount() == 0) {
                                tvStatus.setText("Ban chua co ve nao");
                                rvTickets.setVisibility(View.GONE);
                            } else {
                                tvStatus.setVisibility(View.GONE);
                                rvTickets.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            tvStatus.setText("Khong the dong bo ticket: " + e.getMessage());
                            rvTickets.setVisibility(View.GONE);
                        }
                    });
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ticketsRegistration != null) {
            ticketsRegistration.remove();
            ticketsRegistration = null;
        }
    }
}

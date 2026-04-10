package com.example.cinemaapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private final List<String> seats = new ArrayList<>();
    private final Set<String> selected = new HashSet<>();
    private final Set<String> unavailable = new HashSet<>();
    private final OnSeatChangeListener listener;

    public interface OnSeatChangeListener {
        void onSelectionChanged(Set<String> selectedSeats);
    }

    public SeatAdapter(OnSeatChangeListener listener) {
        this.listener = listener;
    }

    public void setData(List<String> data) {
        seats.clear();
        selected.clear();
        if (data != null) {
            seats.addAll(data);
        }
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    public void setUnavailableSeats(Set<String> unavailableSeats) {
        unavailable.clear();
        if (unavailableSeats != null) {
            unavailable.addAll(unavailableSeats);
        }
        selected.removeAll(unavailable);
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        String seat = seats.get(position);
        holder.tvSeat.setText(seat);
        boolean isUnavailable = unavailable.contains(seat);
        boolean isSelected = selected.contains(seat);
        if (isUnavailable) {
            holder.itemView.setBackgroundColor(Color.parseColor("#555555"));
            holder.tvSeat.setTextColor(Color.parseColor("#A0A0B0"));
        } else if (isSelected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E50914"));
            holder.tvSeat.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#2A2A3A"));
            holder.tvSeat.setTextColor(Color.WHITE);
        }

        holder.itemView.setEnabled(!isUnavailable);
        holder.itemView.setAlpha(isUnavailable ? 0.65f : 1f);
        holder.itemView.setOnClickListener(v -> {
            if (isUnavailable) {
                return;
            }
            if (selected.contains(seat)) {
                selected.remove(seat);
            } else {
                selected.add(seat);
            }
            notifyItemChanged(position);
            notifySelectionChanged();
        });
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    private void notifySelectionChanged() {
        if (listener == null) {
            return;
        }
        List<String> ordered = new ArrayList<>(selected);
        Collections.sort(ordered);
        listener.onSelectionChanged(new HashSet<>(ordered));
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }
    }
}

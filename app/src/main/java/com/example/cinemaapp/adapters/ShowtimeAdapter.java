package com.example.cinemaapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.models.Showtime;
import com.example.cinemaapp.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {
    private final List<Showtime> items = new ArrayList<>();
    private final OnShowtimeClickListener listener;

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    public ShowtimeAdapter(OnShowtimeClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Showtime> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = items.get(position);
        holder.title.setText(showtime.roomName);
        String start = showtime.startTime > 0 ? DateTimeUtils.formatDateTime(showtime.startTime) : "Chua ro gio";
        holder.subtitle.setText(start + " | Con " + showtime.availableSeats + " ghe");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShowtimeClick(showtime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;

        ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}

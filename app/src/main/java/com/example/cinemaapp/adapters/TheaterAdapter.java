package com.example.cinemaapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.models.Theater;
import com.example.cinemaapp.utils.LocalImageResolver;

import java.util.ArrayList;
import java.util.List;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {
    private final List<Theater> items = new ArrayList<>();

    public void setData(List<Theater> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theater_card, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        Theater theater = items.get(position);
        holder.image.setImageResource(LocalImageResolver.resolveTheater(holder.itemView.getContext(), theater.imageUrl));
        holder.title.setText(theater.name);
        holder.subtitle.setText(theater.address);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TheaterViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView subtitle;

        TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivTheater);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}

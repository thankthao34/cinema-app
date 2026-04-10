package com.example.cinemaapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.activities.TicketDetailActivity;
import com.example.cinemaapp.models.Ticket;
import com.example.cinemaapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private final List<Ticket> items = new ArrayList<>();

    public void setData(List<Ticket> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_list, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = items.get(position);

        // Ticket Code
        String ticketCode = ticket.qrCode != null && !ticket.qrCode.isEmpty()
            ? ticket.qrCode
            : ("Ticket #" + ticket.ticketId);
        holder.ticketCode.setText(ticketCode);

        // Movie Title
        String movieTitle = (ticket.movieTitle == null || ticket.movieTitle.isEmpty())
            ? ("Ticket #" + ticket.ticketId)
            : ticket.movieTitle;
        holder.movieTitle.setText(movieTitle);

        // Status
        String statusText = ticket.status != null ? ticket.status : "unknown";
        holder.status.setText("✓");

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TicketDetailActivity.class);
            intent.putExtra(Constants.EXTRA_TICKET_ID, ticket.ticketId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView ticketCode;
        TextView movieTitle;
        TextView status;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketCode = itemView.findViewById(R.id.tvTicketCode);
            movieTitle = itemView.findViewById(R.id.tvMovieTitle);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}

package com.example.cinemaapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.models.Ticket;
import com.example.cinemaapp.services.FirestoreService;
import com.example.cinemaapp.utils.Constants;
import com.example.cinemaapp.utils.DateTimeUtils;

import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        String ticketId = getIntent().getStringExtra(Constants.EXTRA_TICKET_ID);
        if (ticketId == null || ticketId.isEmpty()) {
            finish();
            return;
        }

        loadTicketDetail(ticketId);
    }

    private void loadTicketDetail(String ticketId) {
        FirestoreService service = new FirestoreService();
        service.getTicketById(ticketId, (ticket, error) -> {
            if (error != null || ticket == null) {
                finish();
                return;
            }

            service.enrichTicketWithMovieAndShowtime(ticket, (enrichedTicket, enrichError) -> {
                if (enrichError != null) {
                    finish();
                    return;
                }

                displayTicketDetail(enrichedTicket);
            });
        });
    }

    private void displayTicketDetail(Ticket ticket) {
        TextView tvTicketCode = findViewById(R.id.tvTicketCode);
        LinearLayout layoutDetail = findViewById(R.id.layoutDetail);

        tvTicketCode.setText(ticket.qrCode != null && !ticket.qrCode.isEmpty() ? ticket.qrCode : ticket.ticketId);

        if (layoutDetail != null) {
            layoutDetail.removeAllViews();

            addDetailRow(layoutDetail, "🎬 Phim", ticket.movieTitle != null ? ticket.movieTitle : "N/A");
            addDetailRow(layoutDetail, "👤 Người dùng", ticket.userName != null ? ticket.userName : "N/A");
            addDetailRow(layoutDetail, "📅 Ngày đặt", ticket.bookingTime > 0 ? DateTimeUtils.formatDateTime(ticket.bookingTime) : "N/A");
            String showtimeText = ticket.showtimeStartTime > 0 ? DateTimeUtils.formatDateTime(ticket.showtimeStartTime) : "N/A";
            String roomName = ticket.roomName != null ? ticket.roomName : "Room";
            addDetailRow(layoutDetail, "🎬 Giờ chiếu", showtimeText + " - " + roomName);
            String seatsText = ticket.seats != null && !ticket.seats.isEmpty() ? String.join(", ", ticket.seats) : "N/A";
            addDetailRow(layoutDetail, "🪑 Ghế", seatsText);
            String priceText = String.format(Locale.getDefault(), "%,.0f VND", ticket.totalPrice);
            addDetailRow(layoutDetail, "💰 Tổng tiền", priceText);
            addDetailRow(layoutDetail, "✓ Trạng thái", ticket.status != null ? ticket.status : "Unknown");
        }
    }

    private void addDetailRow(LinearLayout parent, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 12, 0, 12);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(getResources().getColor(R.color.text_secondary));
        tvLabel.setTextSize(12);
        row.addView(tvLabel);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextColor(getResources().getColor(R.color.text_primary));
        tvValue.setTextSize(13);
        tvValue.setPadding(0, 4, 0, 0);
        row.addView(tvValue);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(getResources().getColor(R.color.divider));
        divider.setAlpha(0.3f);
        row.addView(divider);

        parent.addView(row);
    }
}

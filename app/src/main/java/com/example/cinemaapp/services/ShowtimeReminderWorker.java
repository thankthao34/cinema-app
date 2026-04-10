package com.example.cinemaapp.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cinemaapp.utils.Constants;

public class ShowtimeReminderWorker extends Worker {
    public ShowtimeReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String ticketId = getInputData().getString(Constants.EXTRA_TICKET_ID);
        String movieTitle = getInputData().getString("movieTitle");

        if (ticketId == null || ticketId.isEmpty()) {
            return Result.failure();
        }

        NotificationHelper.createChannel(getApplicationContext());
        String title = "Suat chieu sap bat dau";
        String body = (movieTitle == null || movieTitle.isEmpty())
                ? "Phim cua ban se bat dau trong 30 phut"
                : (movieTitle + " se bat dau trong 30 phut");
        NotificationHelper.showNotification(getApplicationContext(), title, body, ticketId);
        return Result.success();
    }
}

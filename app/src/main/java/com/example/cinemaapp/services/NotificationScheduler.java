package com.example.cinemaapp.services;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.cinemaapp.utils.Constants;

import java.util.concurrent.TimeUnit;

public final class NotificationScheduler {
    private NotificationScheduler() {
    }

    public static void scheduleShowtimeReminder(Context context,
                                                String ticketId,
                                                String movieTitle,
                                                long showtimeStartMillis) {
        long delayMillis = showtimeStartMillis - System.currentTimeMillis() - (30 * 60 * 1000L);
        if (delayMillis <= 0) {
            return;
        }

        Data data = new Data.Builder()
                .putString(Constants.EXTRA_TICKET_ID, ticketId)
                .putString("movieTitle", movieTitle == null ? "" : movieTitle)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ShowtimeReminderWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("reminder_" + ticketId)
                .build();

        WorkManager.getInstance(context).enqueue(request);
    }
}

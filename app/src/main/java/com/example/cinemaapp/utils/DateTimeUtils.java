package com.example.cinemaapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {
    private static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private DateTimeUtils() {
    }

    public static String formatDateTime(long millis) {
        return DATE_TIME_FORMAT.format(new Date(millis));
    }
}

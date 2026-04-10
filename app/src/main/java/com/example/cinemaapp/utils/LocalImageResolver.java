package com.example.cinemaapp.utils;

import android.content.Context;

import androidx.annotation.DrawableRes;

import com.example.cinemaapp.R;

public final class LocalImageResolver {
    private static final String PREFIX_LOCAL = "local:";

    private LocalImageResolver() {
    }

    @DrawableRes
    public static int resolvePoster(Context context, String imageRef) {
        return resolve(context, imageRef, R.drawable.img_placeholder_poster);
    }

    @DrawableRes
    public static int resolveTheater(Context context, String imageRef) {
        return resolve(context, imageRef, R.drawable.img_placeholder_theater);
    }

    @DrawableRes
    private static int resolve(Context context, String imageRef, @DrawableRes int fallbackRes) {
        if (imageRef == null || imageRef.trim().isEmpty()) {
            return fallbackRes;
        }

        String name = imageRef.trim();
        if (name.startsWith(PREFIX_LOCAL)) {
            name = name.substring(PREFIX_LOCAL.length());
        }
        int queryIndex = name.indexOf('?');
        if (queryIndex >= 0) {
            name = name.substring(0, queryIndex);
        }
        int slashIndex = name.lastIndexOf('/');
        if (slashIndex >= 0) {
            name = name.substring(slashIndex + 1);
        }
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0) {
            name = name.substring(0, dotIndex);
        }

        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return resId == 0 ? fallbackRes : resId;
    }
}

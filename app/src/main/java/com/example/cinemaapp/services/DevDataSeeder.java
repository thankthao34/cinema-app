package com.example.cinemaapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cinemaapp.BuildConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DevDataSeeder {
    private static final String TAG = "DevDataSeeder";
    private static final String PREF_NAME = "moviemax_dev_seed";
    private static final String KEY_SEEDED_V2 = "seeded_v2";
    private static final String REMOTE_SEED_DOC = "dev_seed_v2";

    private DevDataSeeder() {
    }

    public static void seedIfNeeded(Context context) {
        if (!BuildConfig.DEBUG) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SEEDED_V2, false)) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("app_meta").document(REMOTE_SEED_DOC).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        prefs.edit().putBoolean(KEY_SEEDED_V2, true).apply();
                        return;
                    }
                    seedData(db, prefs);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Cannot check seed status", e));
    }

    private static void seedData(FirebaseFirestore db, SharedPreferences prefs) {
        WriteBatch batch = db.batch();

        batch.set(db.collection("movies").document("m1"), createMovie(
            "m1",
            "Lilo & Stitch",
            "Cuoc phieu luu cam dong giua co be Lilo va sinh vat ngoai hanh tinh Stitch.",
            Arrays.asList("Family", "Adventure", "Comedy"),
            108,
            8.1,
            "P",
            "VI/EN",
                "local:poster_m1_lilo_stitch",
                "local:backdrop_m1_lilo_stitch",
            true,
            false,
            -8
        ), SetOptions.merge());

        batch.set(db.collection("movies").document("m2"), createMovie(
            "m2",
            "How To Train Your Dragon (Live Action)",
            "Hiccup va Rong Rang tro lai trong ban live-action hoanh trang.",
            Arrays.asList("Fantasy", "Adventure", "Action"),
            126,
            8.4,
            "P",
            "EN",
                "local:poster_m2_httyd_live_action",
                "local:backdrop_m2_httyd_live_action",
            true,
            false,
            -3
        ), SetOptions.merge());

        batch.set(db.collection("movies").document("m3"), createMovie(
            "m3",
            "Mission: Impossible - Dead Reckoning",
            "Ethan Hunt doi mat nhiem vu bat kha thi moi nhat voi nhieu pha hanh dong nghat tho.",
            Arrays.asList("Action", "Thriller", "Spy"),
            163,
            8.5,
            "T16",
            "EN",
                "local:poster_m3_mission_impossible",
                "local:backdrop_m3_mission_impossible",
            true,
            false,
            -20
        ), SetOptions.merge());

        batch.set(db.collection("movies").document("m4"), createMovie(
            "m4",
            "Detective Conan Movie",
            "Vu an moi cua tham tu lung danh Conan voi quy mo lon nhat nam.",
            Arrays.asList("Animation", "Mystery", "Crime"),
            112,
            8.2,
            "T13",
            "JP",
                "local:poster_m4_conan_movie",
                "local:backdrop_m4_conan_movie",
            true,
            false,
            -12
        ), SetOptions.merge());

        batch.set(db.collection("movies").document("m5"), createMovie(
            "m5",
            "Doraemon Movie",
            "Nobita va Doraemon bat dau cuoc hanh trinh moi day phep mau va tinh ban.",
            Arrays.asList("Animation", "Family", "Adventure"),
            104,
            7.8,
            "P",
            "JP",
                "local:poster_m5_doraemon_movie",
                "local:backdrop_m5_doraemon_movie",
            true,
            false,
            -6
        ), SetOptions.merge());

        batch.set(db.collection("movies").document("m6"), createMovie(
            "m6",
            "Avengers: Secret Wars",
            "Bo phim sieu anh hung duoc mong cho nhat, sap khoi chieu.",
            Arrays.asList("Action", "Sci-Fi", "Superhero"),
            150,
            0.0,
            "T13",
            "EN",
                "local:poster_m6_avengers_secret_wars",
                "local:backdrop_m6_avengers_secret_wars",
            false,
            true,
            21
        ), SetOptions.merge());

        batch.set(db.collection("theaters").document("t1"), createTheater(
            "t1", "CGV Vincom Ba Trieu", "191 Ba Trieu, Hai Ba Trung, Ha Noi", "Ha Noi",
            21.011, 105.849, "local:theater_t1_batrieu"
        ), SetOptions.merge());
        batch.set(db.collection("theaters").document("t2"), createTheater(
            "t2", "CGV Aeon Mall Tan Phu", "30 Bo Bao Tan Thang, Tan Phu, TP.HCM", "TP.HCM",
            10.803, 106.621, "local:theater_t2_tanphu"
        ), SetOptions.merge());
        batch.set(db.collection("theaters").document("t3"), createTheater(
            "t3", "CGV Landmark 81", "720A Dien Bien Phu, Binh Thanh, TP.HCM", "TP.HCM",
            10.795, 106.721, "local:theater_t3_landmark81"
        ), SetOptions.merge());
        batch.set(db.collection("theaters").document("t4"), createTheater(
            "t4", "CGV Vincom Da Nang", "910A Ngo Quyen, Son Tra, Da Nang", "Da Nang",
            16.072, 108.236, "local:theater_t4_danang"
        ), SetOptions.merge());

        createShowtime(batch, db, "s1", "m1", "t1", "Screen 1 - 2D", 0, 9, 0, 108, 95000, Arrays.asList("A1", "A2", "B4"));
        createShowtime(batch, db, "s2", "m1", "t1", "Screen 1 - 2D", 0, 13, 10, 108, 110000, Arrays.asList("C3", "D5", "E8"));
        createShowtime(batch, db, "s3", "m1", "t2", "Screen 4 - 2D", 0, 18, 30, 108, 120000, Arrays.asList("A6", "B6", "B7"));

        createShowtime(batch, db, "s4", "m2", "t1", "Screen 3 - IMAX", 1, 10, 20, 126, 150000, Arrays.asList("A1", "A3", "F8"));
        createShowtime(batch, db, "s5", "m2", "t3", "Screen IMAX", 1, 16, 0, 126, 165000, Arrays.asList("C1", "C2", "D4", "E7"));
        createShowtime(batch, db, "s6", "m2", "t4", "Screen 2 - 3D", 2, 20, 15, 126, 145000, Arrays.asList("A8", "B1"));

        createShowtime(batch, db, "s7", "m3", "t2", "Screen 5 - 2D", 0, 11, 30, 163, 130000, Arrays.asList("B2", "B3", "C4"));
        createShowtime(batch, db, "s8", "m3", "t3", "Screen 3 - 2D", 1, 15, 45, 163, 140000, Arrays.asList("A4", "D2", "D3"));

        createShowtime(batch, db, "s9", "m4", "t1", "Screen 6 - 2D", 0, 8, 45, 112, 90000, Arrays.asList("A1", "E5"));
        createShowtime(batch, db, "s10", "m4", "t4", "Screen 4 - 2D", 1, 13, 35, 112, 95000, Arrays.asList("C6", "F1"));

        createShowtime(batch, db, "s11", "m5", "t2", "Screen 2 - 2D", 0, 10, 0, 104, 85000, Arrays.asList("A2", "A5"));
        createShowtime(batch, db, "s12", "m5", "t3", "Screen 7 - 2D", 2, 17, 10, 104, 90000, Arrays.asList("D6", "E2"));

        Map<String, Object> meta = new HashMap<>();
        meta.put("version", 2);
        meta.put("createdAt", Timestamp.now());

        batch.set(db.collection("app_meta").document(REMOTE_SEED_DOC), meta, SetOptions.merge());

        batch.commit()
                .addOnSuccessListener(unused -> {
                prefs.edit().putBoolean(KEY_SEEDED_V2, true).apply();
                    Log.d(TAG, "Seed data completed");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Seed data failed", e));
    }

        private static Map<String, Object> createMovie(String movieId,
                               String title,
                               String description,
                               List<String> genre,
                               int duration,
                               double rating,
                               String ageLimit,
                               String language,
                               String posterUrl,
                               String backdropUrl,
                               boolean nowShowing,
                               boolean comingSoon,
                               int releaseOffsetDays) {
        Calendar releaseCal = Calendar.getInstance();
        releaseCal.add(Calendar.DATE, releaseOffsetDays);

        Map<String, Object> movie = new HashMap<>();
        movie.put("movieId", movieId);
        movie.put("title", title);
        movie.put("description", description);
        movie.put("genre", genre);
        movie.put("duration", duration);
        movie.put("rating", rating);
        movie.put("ageLimit", ageLimit);
        movie.put("posterUrl", posterUrl);
        movie.put("trailerUrl", "");
        movie.put("backdropUrl", backdropUrl);
        movie.put("language", language);
        movie.put("releaseDate", new Timestamp(releaseCal.getTime()));
        movie.put("isNowShowing", nowShowing);
        movie.put("isComingSoon", comingSoon);
        return movie;
        }

        private static Map<String, Object> createTheater(String theaterId,
                                 String name,
                                 String address,
                                 String city,
                                 double latitude,
                                 double longitude,
                                 String imageUrl) {
        Map<String, Object> theater = new HashMap<>();
        theater.put("theaterId", theaterId);
        theater.put("name", name);
        theater.put("address", address);
        theater.put("city", city);
        theater.put("latitude", latitude);
        theater.put("longitude", longitude);
        theater.put("imageUrl", imageUrl);
        return theater;
        }

    private static void createShowtime(WriteBatch batch,
                                       FirebaseFirestore db,
                                       String showtimeId,
                                       String movieId,
                                       String theaterId,
                           String roomName,
                           int dayOffset,
                                       int hour,
                                       int minute,
                           int durationMinutes,
                           double price,
                           List<String> bookedSeats) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOffset);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) calendar.clone();
        end.add(Calendar.MINUTE, durationMinutes);

        Map<String, String> seatMap = sampleSeatMap();
        if (bookedSeats != null) {
            for (String seatId : bookedSeats) {
                seatMap.put(seatId, "booked");
            }
        }

        int totalSeats = seatMap.size();
        int bookedCount = 0;
        for (String state : seatMap.values()) {
            if ("booked".equalsIgnoreCase(state)) {
                bookedCount++;
            }
        }

        Map<String, Object> showtime = new HashMap<>();
        showtime.put("showtimeId", showtimeId);
        showtime.put("movieId", movieId);
        showtime.put("theaterId", theaterId);
        showtime.put("roomName", roomName);
        showtime.put("startTime", new Timestamp(calendar.getTime()));
        showtime.put("endTime", new Timestamp(end.getTime()));
        showtime.put("price", price);
        showtime.put("totalSeats", totalSeats);
        showtime.put("availableSeats", Math.max(0, totalSeats - bookedCount));
        showtime.put("seatMap", seatMap);

        batch.set(db.collection("showtimes").document(showtimeId), showtime, SetOptions.merge());
    }

    private static Map<String, String> sampleSeatMap() {
        Map<String, String> seatMap = new HashMap<>();
        for (char row = 'A'; row <= 'F'; row++) {
            for (int col = 1; col <= 8; col++) {
                seatMap.put(row + String.valueOf(col), "available");
            }
        }
        seatMap.put("A1", "booked");
        seatMap.put("B4", "booked");
        seatMap.put("C6", "booked");
        return seatMap;
    }
}

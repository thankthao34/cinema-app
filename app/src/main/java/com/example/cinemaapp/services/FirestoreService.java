package com.example.cinemaapp.services;

import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.models.Showtime;
import com.example.cinemaapp.models.Ticket;
import com.example.cinemaapp.models.User;
import com.example.cinemaapp.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirestoreService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> saveUser(User user) {
        return db.collection(Constants.COLLECTION_USERS).document(user.uid).set(user);
    }

    public Task<Void> saveOrUpdateUserProfile(String uid, String name, String email, String fcmToken) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", uid);
        payload.put("name", name == null ? "" : name);
        payload.put("email", email == null ? "" : email);
        payload.put("fcmToken", fcmToken == null ? "" : fcmToken);
        payload.put("createdAt", FieldValue.serverTimestamp());
        return db.collection(Constants.COLLECTION_USERS).document(uid).set(payload);
    }

    public Task<Void> updateUserFcmToken(String uid, String token) {
        return db.collection(Constants.COLLECTION_USERS).document(uid).update("fcmToken", token);
    }

    public Query getNowShowingMoviesQuery() {
        return db.collection(Constants.COLLECTION_MOVIES)
                .whereEqualTo("isNowShowing", true);
    }

    public Task<Void> createDemoMovieIfEmpty() {
        Movie movie = new Movie("m1", "Avengers: Endgame", "Epic battle", null,
                181, 8.9f, "T13", "", "", "EN", true, false);
        return db.collection(Constants.COLLECTION_MOVIES).document("m1").set(movie);
    }

    public ListenerRegistration listenNowShowingMovies(EventListener<QuerySnapshot> listener) {
        return getNowShowingMoviesQuery().addSnapshotListener(listener);
    }

    public Task<DocumentSnapshot> getMovieById(String movieId) {
        return db.collection(Constants.COLLECTION_MOVIES).document(movieId).get();
    }

    public Task<QuerySnapshot> getShowtimesByMovie(String movieId) {
        return db.collection(Constants.COLLECTION_SHOWTIMES)
                .whereEqualTo("movieId", movieId)
                .get();
    }

    public Task<DocumentSnapshot> getShowtimeById(String showtimeId) {
        return db.collection(Constants.COLLECTION_SHOWTIMES).document(showtimeId).get();
    }

    public Task<String> bookSeatsAndCreateTicket(String userId,
                                                 String movieId,
                                                 String showtimeId,
                                                 List<String> selectedSeats,
                                                 double totalPriceFromClient) {
        return db.runTransaction((Transaction.Function<String>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(
                    db.collection(Constants.COLLECTION_SHOWTIMES).document(showtimeId)
            );

            if (!snapshot.exists()) {
                throw new FirebaseFirestoreException(
                        "Suat chieu khong ton tai",
                        FirebaseFirestoreException.Code.NOT_FOUND
                );
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> seatMapRaw = (Map<String, Object>) snapshot.get("seatMap");
            if (seatMapRaw == null) {
                seatMapRaw = new HashMap<>();
            }

            for (String seat : selectedSeats) {
                String state = String.valueOf(seatMapRaw.get(seat));
                if (!"available".equalsIgnoreCase(state)) {
                    throw new FirebaseFirestoreException(
                            "Ghe " + seat + " da duoc dat",
                            FirebaseFirestoreException.Code.ABORTED
                    );
                }
            }

            for (String seat : selectedSeats) {
                seatMapRaw.put(seat, "booked");
            }

            int availableSeats = valueAsInt(snapshot.get("availableSeats"));
            availableSeats = Math.max(0, availableSeats - selectedSeats.size());

            double showtimePrice = valueAsDouble(snapshot.get("price"));
            double totalPrice = totalPriceFromClient > 0d
                    ? totalPriceFromClient
                    : showtimePrice * selectedSeats.size();

            String theaterId = valueAsString(snapshot.get("theaterId"), "");
            String ticketId = db.collection(Constants.COLLECTION_TICKETS).document().getId();
            Map<String, Object> ticket = new HashMap<>();
            ticket.put("ticketId", ticketId);
            ticket.put("userId", userId);
            ticket.put("movieId", movieId);
            ticket.put("showtimeId", showtimeId);
            ticket.put("theaterId", theaterId);
            ticket.put("seats", selectedSeats);
            ticket.put("totalPrice", totalPrice);
            ticket.put("bookingTime", FieldValue.serverTimestamp());
            ticket.put("status", "confirmed");
            ticket.put("notifSent", false);
            ticket.put("qrCode", String.format(Locale.getDefault(), "MVX-%s", ticketId));

            transaction.update(
                    db.collection(Constants.COLLECTION_SHOWTIMES).document(showtimeId),
                    "seatMap", seatMapRaw,
                    "availableSeats", availableSeats
            );
            transaction.set(db.collection(Constants.COLLECTION_TICKETS).document(ticketId), ticket);

            return ticketId;
        });
    }

    public ListenerRegistration listenTicketsByUser(String userId, EventListener<QuerySnapshot> listener) {
        return db.collection(Constants.COLLECTION_TICKETS)
                .whereEqualTo("userId", userId)
                .addSnapshotListener(listener);
    }

    public List<Movie> mapMovies(QuerySnapshot snapshot) {
        List<Movie> movies = new ArrayList<>();
        if (snapshot == null) {
            return movies;
        }
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Movie movie = doc.toObject(Movie.class);
            if (movie != null) {
                if (movie.movieId == null || movie.movieId.isEmpty()) {
                    movie.movieId = doc.getId();
                }
                movies.add(movie);
            }
        }
        return movies;
    }

    public List<Showtime> mapShowtimes(QuerySnapshot snapshot) {
        List<Showtime> showtimes = new ArrayList<>();
        if (snapshot == null) {
            return showtimes;
        }
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Showtime showtime = new Showtime();
            showtime.showtimeId = valueAsString(doc.get("showtimeId"), doc.getId());
            showtime.movieId = valueAsString(doc.get("movieId"), "");
            showtime.theaterId = valueAsString(doc.get("theaterId"), "");
            showtime.roomName = valueAsString(doc.get("roomName"), "Room");
            showtime.startTime = valueAsMillis(doc.get("startTime"));
            showtime.endTime = valueAsMillis(doc.get("endTime"));
            showtime.price = valueAsDouble(doc.get("price"));
            showtime.totalSeats = valueAsInt(doc.get("totalSeats"));
            showtime.availableSeats = valueAsInt(doc.get("availableSeats"));
            showtime.seatMap = readSeatMap(doc.get("seatMap"));
            showtimes.add(showtime);
        }
        return showtimes;
    }

    public Showtime mapShowtime(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) {
            return null;
        }
        Showtime showtime = new Showtime();
        showtime.showtimeId = valueAsString(doc.get("showtimeId"), doc.getId());
        showtime.movieId = valueAsString(doc.get("movieId"), "");
        showtime.theaterId = valueAsString(doc.get("theaterId"), "");
        showtime.roomName = valueAsString(doc.get("roomName"), "Room");
        showtime.startTime = valueAsMillis(doc.get("startTime"));
        showtime.endTime = valueAsMillis(doc.get("endTime"));
        showtime.price = valueAsDouble(doc.get("price"));
        showtime.totalSeats = valueAsInt(doc.get("totalSeats"));
        showtime.availableSeats = valueAsInt(doc.get("availableSeats"));
        showtime.seatMap = readSeatMap(doc.get("seatMap"));
        return showtime;
    }

    public List<Ticket> mapTickets(QuerySnapshot snapshot) {
        List<Ticket> tickets = new ArrayList<>();
        if (snapshot == null) {
            return tickets;
        }
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Ticket ticket = new Ticket();
            ticket.ticketId = valueAsString(doc.get("ticketId"), doc.getId());
            ticket.userId = valueAsString(doc.get("userId"), "");
            ticket.movieId = valueAsString(doc.get("movieId"), "");
            ticket.showtimeId = valueAsString(doc.get("showtimeId"), "");
            ticket.theaterId = valueAsString(doc.get("theaterId"), "");
            ticket.status = valueAsString(doc.get("status"), "confirmed");
            ticket.totalPrice = valueAsDouble(doc.get("totalPrice"));
            ticket.bookingTime = valueAsMillis(doc.get("bookingTime"));
            ticket.seats = readSeatsList(doc.get("seats"));
            tickets.add(ticket);
        }
        return tickets;
    }

    public interface TicketsEnrichedCallback {
        void onSuccess(List<Ticket> tickets);

        void onError(Exception e);
    }

    public void enrichTicketsWithMovieAndShowtime(List<Ticket> tickets, TicketsEnrichedCallback callback) {
        if (tickets == null || tickets.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        Map<String, Task<DocumentSnapshot>> movieTasks = new HashMap<>();
        Map<String, Task<DocumentSnapshot>> showtimeTasks = new HashMap<>();
        Map<String, Task<DocumentSnapshot>> userTasks = new HashMap<>();
        List<Task<?>> allTasks = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.movieId != null && !ticket.movieId.isEmpty() && !movieTasks.containsKey(ticket.movieId)) {
                Task<DocumentSnapshot> task = db.collection(Constants.COLLECTION_MOVIES).document(ticket.movieId).get();
                movieTasks.put(ticket.movieId, task);
                allTasks.add(task);
            }
            if (ticket.showtimeId != null && !ticket.showtimeId.isEmpty() && !showtimeTasks.containsKey(ticket.showtimeId)) {
                Task<DocumentSnapshot> task = db.collection(Constants.COLLECTION_SHOWTIMES).document(ticket.showtimeId).get();
                showtimeTasks.put(ticket.showtimeId, task);
                allTasks.add(task);
            }
            if (ticket.userId != null && !ticket.userId.isEmpty() && !userTasks.containsKey(ticket.userId)) {
                Task<DocumentSnapshot> task = db.collection(Constants.COLLECTION_USERS).document(ticket.userId).get();
                userTasks.put(ticket.userId, task);
                allTasks.add(task);
            }
        }

        Tasks.whenAllComplete(allTasks)
                .addOnSuccessListener(ignored -> {
                    for (Ticket ticket : tickets) {
                        // Get movie title
                        DocumentSnapshot movieDoc = null;
                        Task<DocumentSnapshot> movieTask = movieTasks.get(ticket.movieId);
                        if (movieTask != null && movieTask.isSuccessful()) {
                            movieDoc = movieTask.getResult();
                        }
                        if (movieDoc != null && movieDoc.exists()) {
                            ticket.movieTitle = valueAsString(movieDoc.get("title"), ticket.movieId);
                        }

                        // Get showtime start time and room name
                        DocumentSnapshot showtimeDoc = null;
                        Task<DocumentSnapshot> showtimeTask = showtimeTasks.get(ticket.showtimeId);
                        if (showtimeTask != null && showtimeTask.isSuccessful()) {
                            showtimeDoc = showtimeTask.getResult();
                        }
                        if (showtimeDoc != null && showtimeDoc.exists()) {
                            ticket.showtimeStartTime = valueAsMillis(showtimeDoc.get("startTime"));
                            ticket.roomName = valueAsString(showtimeDoc.get("roomName"), "Room");
                        }

                        // Get user name
                        DocumentSnapshot userDoc = null;
                        Task<DocumentSnapshot> userTask = userTasks.get(ticket.userId);
                        if (userTask != null && userTask.isSuccessful()) {
                            userDoc = userTask.getResult();
                        }
                        if (userDoc != null && userDoc.exists()) {
                            ticket.userName = valueAsString(userDoc.get("name"), "N/A");
                        }
                    }
                    callback.onSuccess(tickets);
                })
                .addOnFailureListener(callback::onError);
    }

    private String valueAsString(Object value, String fallback) {
        return value == null ? fallback : String.valueOf(value);
    }

    private int valueAsInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    private double valueAsDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0d;
    }

    private long valueAsMillis(Object value) {
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toDate().getTime();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    private Map<String, String> readSeatMap(Object value) {
        Map<String, String> seatMap = new HashMap<>();
        if (!(value instanceof Map)) {
            return seatMap;
        }
        Map<?, ?> raw = (Map<?, ?>) value;
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String state = entry.getValue() == null ? "available" : String.valueOf(entry.getValue());
            seatMap.put(key, state);
        }
        return seatMap;
    }

    private List<String> readSeatsList(Object value) {
        List<String> seats = new ArrayList<>();
        if (value instanceof List) {
            List<?> raw = (List<?>) value;
            for (Object item : raw) {
                if (item != null) {
                    seats.add(String.valueOf(item));
                }
            }
        }
        return seats;
    }

    public void getTicketById(String ticketId, OnTicketLoadedCallback callback) {
        db.collection(Constants.COLLECTION_TICKETS).document(ticketId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Ticket ticket = new Ticket();
                        ticket.ticketId = valueAsString(doc.get("ticketId"), doc.getId());
                        ticket.userId = valueAsString(doc.get("userId"), "");
                        ticket.movieId = valueAsString(doc.get("movieId"), "");
                        ticket.showtimeId = valueAsString(doc.get("showtimeId"), "");
                        ticket.theaterId = valueAsString(doc.get("theaterId"), "");
                        ticket.status = valueAsString(doc.get("status"), "confirmed");
                        ticket.totalPrice = valueAsDouble(doc.get("totalPrice"));
                        ticket.bookingTime = valueAsMillis(doc.get("bookingTime"));
                        ticket.seats = readSeatsList(doc.get("seats"));
                        ticket.qrCode = valueAsString(doc.get("qrCode"), "");
                        callback.onSuccess(ticket, null);
                    } else {
                        callback.onSuccess(null, new Exception("Ticket not found"));
                    }
                })
                .addOnFailureListener(e -> callback.onSuccess(null, e));
    }

    public void enrichTicketWithMovieAndShowtime(Ticket ticket, OnTicketEnrichedCallback callback) {
        if (ticket == null) {
            callback.onSuccess(null, new Exception("Ticket is null"));
            return;
        }

        List<Task<?>> allTasks = new ArrayList<>();
        final Task<DocumentSnapshot>[] movieTask = new Task[1];
        final Task<DocumentSnapshot>[] showtimeTask = new Task[1];
        final Task<DocumentSnapshot>[] userTask = new Task[1];

        if (ticket.movieId != null && !ticket.movieId.isEmpty()) {
            movieTask[0] = db.collection(Constants.COLLECTION_MOVIES).document(ticket.movieId).get();
            allTasks.add(movieTask[0]);
        }
        if (ticket.showtimeId != null && !ticket.showtimeId.isEmpty()) {
            showtimeTask[0] = db.collection(Constants.COLLECTION_SHOWTIMES).document(ticket.showtimeId).get();
            allTasks.add(showtimeTask[0]);
        }
        if (ticket.userId != null && !ticket.userId.isEmpty()) {
            userTask[0] = db.collection(Constants.COLLECTION_USERS).document(ticket.userId).get();
            allTasks.add(userTask[0]);
        }

        if (allTasks.isEmpty()) {
            callback.onSuccess(ticket, null);
            return;
        }

        Tasks.whenAllComplete(allTasks)
                .addOnSuccessListener(ignored -> {
                    if (movieTask[0] != null && movieTask[0].isSuccessful()) {
                        DocumentSnapshot movieDoc = movieTask[0].getResult();
                        if (movieDoc != null && movieDoc.exists()) {
                            ticket.movieTitle = valueAsString(movieDoc.get("title"), "");
                        }
                    }

                    if (showtimeTask[0] != null && showtimeTask[0].isSuccessful()) {
                        DocumentSnapshot showtimeDoc = showtimeTask[0].getResult();
                        if (showtimeDoc != null && showtimeDoc.exists()) {
                            ticket.showtimeStartTime = valueAsMillis(showtimeDoc.get("startTime"));
                            ticket.roomName = valueAsString(showtimeDoc.get("roomName"), "Room");
                        }
                    }

                    if (userTask[0] != null && userTask[0].isSuccessful()) {
                        DocumentSnapshot userDoc = userTask[0].getResult();
                        if (userDoc != null && userDoc.exists()) {
                            ticket.userName = valueAsString(userDoc.get("name"), "N/A");
                        }
                    }

                    callback.onSuccess(ticket, null);
                })
                .addOnFailureListener(e -> callback.onSuccess(null, e));
    }

    public interface OnTicketLoadedCallback {
        void onSuccess(Ticket ticket, Exception error);
    }

    public interface OnTicketEnrichedCallback {
        void onSuccess(Ticket ticket, Exception error);
    }
}

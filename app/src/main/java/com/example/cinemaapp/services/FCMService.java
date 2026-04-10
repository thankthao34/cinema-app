package com.example.cinemaapp.services;

import androidx.annotation.NonNull;

import com.example.cinemaapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("fcmToken", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = "MovieMax";
        String body = "Ban co thong bao moi";
        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
        }

        String ticketId = "ticket";
        if (message.getData().containsKey(Constants.EXTRA_TICKET_ID)) {
            ticketId = message.getData().get(Constants.EXTRA_TICKET_ID);
        } else if (message.getData().containsKey("ticketId")) {
            ticketId = message.getData().get("ticketId");
        }

        NotificationHelper.showNotification(this, title, body, ticketId);
    }
}

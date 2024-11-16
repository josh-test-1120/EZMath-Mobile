package com.example.ezmathmobile.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Firebase Cloud Messaging Service to handle message
 * events from firebase
 */
public class MessagingService extends FirebaseMessagingService {

    /**
     * This is an override of the onNewToken method
     * @param token The token used for sending messages to this application instance. This token is
     *     the same as the one retrieved by {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token: " + token);
    }

    /**
     * This is an override of the onMessageReceived method
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM","460 Message: " + message.getNotification().getBody());
    }
}

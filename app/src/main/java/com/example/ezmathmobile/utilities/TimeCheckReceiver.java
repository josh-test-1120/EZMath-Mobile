package com.example.ezmathmobile.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Perform your action
        Log.d("TimeCheckReceiver", "It's the right time! Performing action.");
    }
}



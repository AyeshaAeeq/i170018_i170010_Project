package com.namrahrasool.i170018_i170010_project;

import android.content.Context;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyNotificationReceiveHandler implements OneSignal.NotificationReceivedHandler {
    Context c;
    public MyNotificationReceiveHandler(Context c){
        this.c=c;
    }
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }
    }
}
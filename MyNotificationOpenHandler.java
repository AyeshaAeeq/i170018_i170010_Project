package com.namrahrasool.i170018_i170010_project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyNotificationOpenHandler implements OneSignal.NotificationOpenedHandler {

    Context c;
    FirebaseAuth mAuth;
    public MyNotificationOpenHandler(Context c){
        this.c=c;
    }
    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;
        Log.i("OSNotificationPayload", "result.notification.payload.toJSONObject().toString(): " + result.notification.payload.toJSONObject().toString());


        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        /*mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            try {
                Intent intent1 = new Intent(c, ChatActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("userid", data.get("userid").toString());
                intent1.putExtra("playerid", data.get("playerid").toString());
                c.startActivity(intent1);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            Intent intent = new Intent(c, SigninActivity.class);//put an if check for different type of notifications
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
        }*/
    }
}

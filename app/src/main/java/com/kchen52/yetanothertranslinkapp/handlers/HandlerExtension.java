package com.kchen52.yetanothertranslinkapp.handlers;

import android.os.Handler;
import android.os.Message;



import java.lang.ref.WeakReference;

// For handling responses from the requestHandler
/*public class HandlerExtension extends Handler {
    private final String TAG = "HandlerExtension";
    private final WeakReference<MapsActivity> currentActivity;

    public HandlerExtension(MapsActivity activity) {
        currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        MapsActivity mapsActivity = currentActivity.get();
        if (mapsActivity != null) {
            mapsActivity.updateDisplayedTimeAndDrawBuses(msg);
        }
    }
}*/

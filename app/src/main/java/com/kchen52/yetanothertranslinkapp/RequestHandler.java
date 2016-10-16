package com.kchen52.yetanothertranslinkapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

public class RequestHandler {
    private SharedPreferences sharedPref;
    private Context appContext;

    private String TWILIO_NUMBER;
    private String busesRequested;


    public RequestHandler(Context applicationContext) {
        appContext = applicationContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        update();
    }

    public void update() {
        TWILIO_NUMBER = sharedPref.getString(appContext.getString(R.string.saved_twilio_number), appContext.getString(R.string.saved_twilio_number_default));
        busesRequested = sharedPref.getString(appContext.getString(R.string.saved_buses_requested), appContext.getString(R.string.saved_buses_requested_default));
    }

    public String getBusesRequested() {
        return busesRequested;
    }

    public String getTwilioNumber() {
        return TWILIO_NUMBER;
    }

    public void sendSMS(String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(TWILIO_NUMBER, null, msg, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasActiveInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

package com.kchen52.yetanothertranslinkapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public BusHandler updateWithInternet() {
        final BusHandler newBusHandler = new BusHandler(appContext);
        final String[] buses = busesRequested.split(", ");
        if (buses.length == 0) { return newBusHandler; }

        Thread requestThread = new Thread() {
            public void run() {
                for (String bus : buses) {
                    String busRequestURL = "http://api.translink.ca/rttiapi/v1/buses?apikey=1517ba37nS64aO4ZjqBD" +
                            "&routeNo=" + bus;
                    String GETRequestResult = sendGetRequest(busRequestURL);

                    Pattern busPattern = Pattern.compile("<Bus>(.*?)</Bus>");
                    Matcher matcher = busPattern.matcher(GETRequestResult);
                    while (matcher.find()) {
                        Bus newBus = new Bus();
                        newBus.init(matcher.group());
                        newBusHandler.addBus(newBus);
                    }
                }
                newBusHandler.setLastUpdatedTime(new Date());
            }
        };
        requestThread.start();

        // TODO: Catch timeout events
        try {
            requestThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newBusHandler;
    }


    private String sendGetRequest(String urlToRead) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String currentLine = "";
            while ((currentLine = reader.readLine()) != null) {
                result.append(currentLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "No results available";
        }
        return result.toString();
    }
}

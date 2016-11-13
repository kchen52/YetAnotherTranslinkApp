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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler {
    private SharedPreferences sharedPref;
    private Context appContext;

    // TODO: Separate sharedpref behaviour out into its own class?
    private String TWILIO_NUMBER;
    private String TRANSLINK_API;
    private String busesRequested;
    private boolean useInternetIfAvailable;

    public RequestHandler(Context applicationContext) {
        appContext = applicationContext;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        update();
    }

    // Updates internal values from sharedPreferences
    public void update() {
        TWILIO_NUMBER = sharedPref.getString(appContext.getString(R.string.saved_twilio_number), appContext.getString(R.string.saved_twilio_number_default));
        busesRequested = sharedPref.getString(appContext.getString(R.string.saved_buses_requested), appContext.getString(R.string.saved_buses_requested_default));
        useInternetIfAvailable = sharedPref.getBoolean(appContext.getString(R.string.use_internet_if_available), true);
        TRANSLINK_API = sharedPref.getString(appContext.getString(R.string.translink_api), appContext.getString(R.string.translink_api_default));
    }

    public String getBusesRequested() {
        return busesRequested;
    }

    public String getTwilioNumber() {
        return TWILIO_NUMBER;
    }

    public boolean getUseInternet() { return useInternetIfAvailable; }

    public void sendSMS(String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(TWILIO_NUMBER, null, msg, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasTranslinkAPI() {
        return !TRANSLINK_API.equals("");
    }

    // Returns true if the device has an internet connection, though it doesn't guarantee that
    // the connection is working.
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
                    try {
                        String busRequestURL = "http://api.translink.ca/rttiapi/v1/buses?apikey=" + TRANSLINK_API +
                                "&routeNo=" + bus;
                        String GETRequestResult = sendGetRequest(busRequestURL);

                        Pattern busPattern = Pattern.compile("<Bus>(.*?)</Bus>");
                        Matcher matcher = busPattern.matcher(GETRequestResult);
                        while (matcher.find()) {
                            Bus newBus = new Bus();
                            newBus.init(matcher.group());
                            newBusHandler.addBus(newBus);
                        }
                    } catch (SocketTimeoutException e) {
                        // Timeout occurred for that URL, so don't add a bus in this case
                    }
                }
                newBusHandler.setLastUpdatedTime(new Date());
            }
        };
        requestThread.start();

        try {
            requestThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newBusHandler;
    }


    private String sendGetRequest(String urlToRead) throws SocketTimeoutException {
        StringBuilder result = new StringBuilder();

        // Time to wait before deciding that we've timed out
        int TIMEOUT_VALUE = 1000;
        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(TIMEOUT_VALUE);
            conn.setReadTimeout(TIMEOUT_VALUE);
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                result.append(currentLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return result.toString();
    }
}

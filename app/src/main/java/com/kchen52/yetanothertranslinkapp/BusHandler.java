package com.kchen52.yetanothertranslinkapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusHandler {
    private LinkedList<Bus> buses;
    private Date lastUpdatedTime;
    private Context appContext;

    public BusHandler(Context applicationContext) {
        buses = new LinkedList<>();
        appContext = applicationContext;
    }
    public LinkedList<Bus> getBuses() {
        return buses;
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }
    public void setLastUpdatedTime(Date date) {
        lastUpdatedTime = date;
    }

    // Takes the incoming SMS message, parses it and saves the information in the buses linkedlist
    public void updateBuses(String message) {
        buses = parseMessage(message);
        lastUpdatedTime = new Date();
    }

    private LinkedList<Bus> parseMessage(String input) {
        // At this stage, input can look like
        // GUILDFORD>8122:-122.842117,-122.842117|8123:-122.123456,123.123456|NEWTON EXCH>8123:122.80325,-122.80325|
        // Currently, we want to split it by destination
        // E.g, first match would return GUILDFORD>...
        // and second match would return NEWTON EXCH>...
        Pattern busPattern = Pattern.compile("([\\w\\s]*)>((\\d)+:-?(\\d)*\\.(\\d)+,-?(\\d)*\\.(\\d)+\\|)+");
        Matcher matcher = busPattern.matcher(input);

        LinkedList<Bus> listOfBuses = new LinkedList<>();
        while (matcher.find()) {
            String allBusInformation = matcher.group();
            String destination = allBusInformation.split(">")[0];
            String individualBusInformation = allBusInformation.split(">")[1];

            // Now we're dealing with something like
            // 8122:-122.842117,-122.842117|8123:-122.123456,123.123456|
            // This time, we want to split it by bus vehicle number
            // E.g., 8122:...
            // 8123:...
            Pattern individualBusPattern = Pattern.compile("(\\d)*:-?(\\d)*\\.(\\d)*,-?(\\d)*\\.(\\d)*");
            Matcher individualMatcher = individualBusPattern.matcher(individualBusInformation);

            while (individualMatcher.find()) {
                Bus bus = new Bus();
                bus.init(destination, individualMatcher.group());
                listOfBuses.add(bus);
            }
        }
        return listOfBuses;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void updateWithLastText(String twilioNumber) {
        String lastText = readLastYATAText(twilioNumber);
        if (!lastText.equals("")) {
            // Provided it's not empty, parse the message, draw buses, and update last updated time
            String unreadableDate = lastText.split("\\{")[0];
            String bodyOfText = lastText.split("\\{")[1];
            // Just to get rid of that last "}"
            bodyOfText = bodyOfText.substring(0, bodyOfText.length()-1);
            lastUpdatedTime = new Date(Long.parseLong(unreadableDate));
            buses = parseMessage(bodyOfText);
        }
    }

    private String readLastYATAText(String twilioNumber) {
        // Reads the inbox for the last message sent from the server if it exists
        String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
        //Cursor cursor = appContext.getContentResolver().query(Uri.parse("content://sms/inbox"), projection, "address=\'"+TWILIO_NUMBER+"\'", null, "date desc limit 1");
        Cursor cursor = appContext.getContentResolver().query(Uri.parse("content://sms/inbox"), projection, "address=\'" + twilioNumber + "\'", null, "date desc limit 1");

        StringBuilder builder = new StringBuilder();
        if (cursor.moveToFirst()) {
            int indexBody = cursor.getColumnIndex("body");
            int indexDate = cursor.getColumnIndex("date");
            builder.append(cursor.getString(indexDate));
            builder.append("{");
            builder.append(cursor.getString(indexBody));
            builder.append("}");
        }

        if (!cursor.isClosed()) { cursor.close(); }
        return builder.toString();
    }
}

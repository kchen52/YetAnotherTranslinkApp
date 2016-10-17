package com.kchen52.yetanothertranslinkapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kevin on 2016-09-09.
 */
public class Bus {
    private String destination;
    private int vehicleNumber;
    private double longitude;
    private double latitude;

    public Bus() {
        destination = "null";
        vehicleNumber = -1;
        longitude = 0.0;
        latitude = 0.0;
    }

    // Basically the same algorithm that the servlet uses
    public void init(String rawInfo) {
        destination = setValue("Destination", rawInfo);
        vehicleNumber = Integer.parseInt(setValue("VehicleNo", rawInfo));
        longitude = Double.parseDouble(setValue("Longitude", rawInfo));
        latitude = Double.parseDouble(setValue("Latitude", rawInfo));
    }

    private String setValue(String valueToSet, String input) {
        Pattern pattern = Pattern.compile("<" + valueToSet + ">(.+?)</" + valueToSet + ">");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        return matcher.group(1);
    }

    public void init(String busDestination, String input) {
        //Note: input is in the form 8122:-122.842117,-122.842117
        // Where the long/lat value is Lat, and second is Long
        String[] initialSplit = input.split(":");
        vehicleNumber = Integer.parseInt(initialSplit[0]);
        destination = busDestination;

        String longAndLat = initialSplit[1];
        latitude = Double.parseDouble(longAndLat.split(",")[0]);
        longitude = Double.parseDouble(longAndLat.split(",")[1]);
    }

    public String getDestination() {
        return destination;
    }
    public int getVehicleNumber() {
        return vehicleNumber;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }

}

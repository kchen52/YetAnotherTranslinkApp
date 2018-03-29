package com.kchen52.yetanothertranslinkapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bus {
    private String destination;
    private int vehicleNumber;
    private double longitude;
    private double latitude;
    private String direction;

    public Bus() {
        destination = "null";
        vehicleNumber = -1;
        longitude = 0.0;
        latitude = 0.0;
        direction = "West";
    }

    // Basically the same algorithm that the servlet uses
    public void init(String rawInfo) {
        try {
            destination = setValue("Destination", rawInfo);
            vehicleNumber = Integer.parseInt(setValue("VehicleNo", rawInfo));
            longitude = Double.parseDouble(setValue("Longitude", rawInfo));
            latitude = Double.parseDouble(setValue("Latitude", rawInfo));
            direction = setValue("Direction", rawInfo);
        } catch (IllegalStateException e) {
            // This happens if the init string is in an invalid format. Set all members to their
            // default values.
            destination = "null";
            vehicleNumber = -1;
            longitude = 0.0;
            latitude = 0.0;
            direction = "West";
        }
    }

    private String setValue(String valueToSet, String input) {
        Pattern pattern = Pattern.compile("<" + valueToSet + ">(.+?)</" + valueToSet + ">");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        return matcher.group(1);
    }

    // This one should only be used for SMS. Since I'm removing that soon this won't be used
    public void init(String busDestination, String input) {
        try {
            //Note: input is in the form 8122:-122.842117,-122.842117
            // Where the long/lat value is Lat, and second is Long
            String[] initialSplit = input.split(":");
            vehicleNumber = Integer.parseInt(initialSplit[0]);
            destination = busDestination;

            String longAndLat = initialSplit[1];
            latitude = Double.parseDouble(longAndLat.split(",")[0]);
            longitude = Double.parseDouble(longAndLat.split(",")[1]);
        } catch (IllegalStateException | NumberFormatException e) {
            // This happens if the init strings is in an invalid format. Set all members to their
            // default values.
            destination = "null";
            vehicleNumber = -1;
            longitude = 0.0;
            latitude = 0.0;
        }
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

package com.kchen52.yetanothertranslinkapp;

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

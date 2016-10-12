package com.kchen52.yetanothertranslinkapp;

import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kevin on 2016-10-12.
 */
public class BusHandler {
    private LinkedList<Bus> buses;
    private Date lastUpdatedTime;

    public BusHandler() {
        buses = new LinkedList<>();
    }
    public LinkedList<Bus> getBuses() {
        return buses;
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
}

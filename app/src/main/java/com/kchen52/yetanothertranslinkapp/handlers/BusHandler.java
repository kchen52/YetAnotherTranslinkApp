package com.kchen52.yetanothertranslinkapp.handlers;

import android.os.Parcel;
import android.os.Parcelable;

import com.kchen52.yetanothertranslinkapp.Bus;

import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusHandler implements Parcelable {
    private LinkedList<Bus> buses;
    private Date lastUpdatedTime;

    public BusHandler() {
        buses = new LinkedList<>();
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

    // Parcelling methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[] {
                this.buses,
                this.lastUpdatedTime,
        });
    }

    public BusHandler(Parcel in) {
        Object[] members = in.readArray(null);
        this.buses = (LinkedList<Bus>) members[0];
        this.lastUpdatedTime = (Date) members[1];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BusHandler createFromParcel(Parcel in) {
            return new BusHandler(in);
        }

        @Override
        public BusHandler[] newArray(int size) {
            return new BusHandler[0];
        }
    };
}

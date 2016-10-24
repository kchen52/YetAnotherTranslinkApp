package com.kchen52.yetanothertranslinkapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBus {
    @Test
    public void oneParameterLegitimateInit() {
        String exampleInput = "<Bus><VehicleNo>7270</VehicleNo><TripId>8175527</TripId><RouteNo>320</RouteNo><Direction>WEST</Direction><Destination>SURREY CTRL STN</Destination>" +
                "<Pattern>WB1L</Pattern><Latitude>49.106217</Latitude><Longitude>-122.653733</Longitude><RecordedTime>02:37:20 pm</RecordedTime><RouteMap>" +
                "<Href>http://nb.translink.ca/geodata/320.kmz</Href></RouteMap></Bus>";
        Bus bus = new Bus();
        bus.init(exampleInput);

        assertEquals(bus.getVehicleNumber(), 7270);
        assertEquals(bus.getDestination(), "SURREY CTRL STN");
        assertEquals(bus.getLatitude(), 49.106217, 0.0000001);
        assertEquals(bus.getLongitude(), -122.653733, 0.0000001);
    }

    @Test
    public void twoParameterLegitimateInit() {
        Bus bus = new Bus();
        bus.init("FLEETWOOD", "7953:49.188817,-122.848233");

        assertEquals(bus.getVehicleNumber(), 7953);
        assertEquals(bus.getDestination(), "FLEETWOOD");
        assertEquals(bus.getLatitude(), 49.188817, 0.0000001);
        assertEquals(bus.getLongitude(), -122.848233, 0.0000001);

        bus.init("SANTA'S HOUSE", "123:859,1.123456");

        assertEquals(bus.getVehicleNumber(), 123);
        assertEquals(bus.getDestination(), "SANTA'S HOUSE");
        assertEquals(bus.getLatitude(), 859, 0.0000001);
        assertEquals(bus.getLongitude(), 1.123456, 0.0000001);
    }

    @Test
    public void emptyStringInit() {
        Bus bus = new Bus();
        // Test one param init first
        bus.init("");
        assertEquals(bus.getVehicleNumber(), -1);
        assertEquals(bus.getDestination(), "null");
        assertEquals(bus.getLatitude(), 0.0, 0.0000001);
        assertEquals(bus.getLongitude(), 0.0, 0.0000001);

        // Then test two param init
        bus.init("", "");
        assertEquals(bus.getVehicleNumber(), -1);
        assertEquals(bus.getDestination(), "null");
        assertEquals(bus.getLatitude(), 0.0, 0.0000001);
        assertEquals(bus.getLongitude(), 0.0, 0.0000001);
    }

    @Test
    public void oneParameterBadInit() {
        Bus bus = new Bus();
        bus.init("huehuehuehuehuehue");
        assertEquals(bus.getVehicleNumber(), -1);
        assertEquals(bus.getDestination(), "null");
        assertEquals(bus.getLatitude(), 0.0, 0.0000001);
        assertEquals(bus.getLongitude(), 0.0, 0.0000001);
    }

    @Test
    public void twoParameterBadInit() {
        Bus bus = new Bus();
        bus.init("huehuehuehuehuehue", "123123j123i");
        assertEquals(bus.getVehicleNumber(), -1);
        assertEquals(bus.getDestination(), "null");
        assertEquals(bus.getLatitude(), 0.0, 0.0000001);
        assertEquals(bus.getLongitude(), 0.0, 0.0000001);

        bus.init("123123j123i", "asd81hvnz:\\");
        assertEquals(bus.getVehicleNumber(), -1);
        assertEquals(bus.getDestination(), "null");
        assertEquals(bus.getLatitude(), 0.0, 0.0000001);
        assertEquals(bus.getLongitude(), 0.0, 0.0000001);
    }
}

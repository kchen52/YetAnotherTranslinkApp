package com.kchen52.yetanothertranslinkapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBus {
    @Test
    public void twoParameterInit() {
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
}

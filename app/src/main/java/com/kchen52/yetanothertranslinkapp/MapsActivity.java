package com.kchen52.yetanothertranslinkapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BroadcastReceiver smsReceiver;

    private final String NUMBER_TO_RESPOND_TO = "7786554235";
    private final boolean TWILIO_TRIAL_ACCOUNT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng defaultMapLocation = new LatLng(49.118641, -122.747700);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultMapLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultMapLocation, 12.0f));

        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SMSReceiver();
        registerReceiver(smsReceiver, smsFilter);
    }

    private class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();
                        if (phoneNumber.equals(NUMBER_TO_RESPOND_TO)) {
                            // Then we do our parsing shit
                            message = cleanSMS(message);
                            ArrayList<Bus> busesToDraw = getBuses(message);

                            for (Bus bus : busesToDraw) {
                                Log.i("Drawing the following:", "Destination: " + bus.getDestination() + ", VehicleNo: " + bus.getVehicleNumber() +
                                ", Longitude: " + bus.getLongitude() + ", Latitude: " + bus.getLatitude());
                                addMarker(bus);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMarker(Bus bus) {
        LatLng busLocation = new LatLng(bus.getLatitude(), bus.getLongitude());
        mMap.addMarker(new MarkerOptions().position(busLocation).title(bus.getDestination() + ":" + bus.getVehicleNumber()));
    }

    private ArrayList<Bus> getBuses(String input) {
        // At this stage, input can look like
        // GUILDFORD>8122:-122.842117,-122.842117|8123:-122.123456,123.123456|NEWTON EXCH>8123:122.80325,-122.80325|
        // Currently, we want to split it by destination
        // E.g, first match would return GUILDFORD>...
        // and second match would return NEWTON EXCH>...
        Pattern busPattern = Pattern.compile("([\\w\\s]*)>((\\d)+:-?(\\d)*\\.(\\d)+,-?(\\d)*\\.(\\d)+\\|)+");
        Matcher matcher = busPattern.matcher(input);

        ArrayList<Bus> listOfBuses = new ArrayList<Bus>();
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

    private String cleanSMS(String incomingSMS) {
        // First, get rid of the Twilio prepended msg if this is a trial account
        String twilioTrialMessage = "Sent from your Twilio trial account - ";
        if (TWILIO_TRIAL_ACCOUNT) {
            return incomingSMS.split(twilioTrialMessage)[1];
        } else {
            return incomingSMS;
        }
    }
}

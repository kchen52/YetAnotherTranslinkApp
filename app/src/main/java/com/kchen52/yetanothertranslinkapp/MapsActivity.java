package com.kchen52.yetanothertranslinkapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BroadcastReceiver smsReceiver;

    private final String TWILIO_NUMBER = "+17786554235";

    private String busesRequested = "320";

    // Used for formatting time/date for display
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
    private String lastRequestedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bus_list:
                Toast.makeText(getApplicationContext(), "bus list", Toast.LENGTH_LONG).show();
                return true;
            case R.id.refresh:
                Toast.makeText(getApplicationContext(), "refresh", Toast.LENGTH_LONG).show();
                return true;
            case R.id.settings:
                Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

                    // Required because the following for loop doesn't go through the entire thing at once
                    StringBuilder entireSMS = new StringBuilder();
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        //Toast.makeText(context, "Number: " + phoneNumber, Toast.LENGTH_LONG).show();
                        if (phoneNumber.equals(TWILIO_NUMBER)) {
                            String message = currentMessage.getDisplayMessageBody();
                            entireSMS.append(message);
                        }
                    }
                    // Parse the information, and store it as Bus objects
                    ArrayList<Bus> busesToDraw = getBuses(entireSMS.toString());

                    // NOTE: If each bus information is sent in its own text (e.g., a text for 320, one for 099, etc.)
                    // each successive text will wipe out previous texts. This is currently working under the assumption
                    // that all information comes in one text.
                    // If there are new buses, wipe the old ones from the map
                    if (busesToDraw.size() != 0) {
                       mMap.clear();
                    }
                    for (Bus bus : busesToDraw) {
                        Log.i("Drawing the following:", "Destination: " + bus.getDestination() + ", VehicleNo: " + bus.getVehicleNumber() +
                                ", Longitude: " + bus.getLongitude() + ", Latitude: " + bus.getLatitude());
                        addMarker(bus);
                    }

                    // Update requested time
                    Date date = new Date();
                    lastRequestedTime = dateFormat.format(date);
                    TextView lastRequestedTime_TextView= (TextView)findViewById(R.id.lastRequestDateTextView);
                    lastRequestedTime_TextView.setText("Last updated: " + lastRequestedTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMarker(Bus bus) {
        LatLng busLocation = new LatLng(bus.getLatitude(), bus.getLongitude());
        mMap.addMarker(new MarkerOptions().position(busLocation)
                        .title(bus.getDestination() + ":" + bus.getVehicleNumber())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp)));
    }

    public void requestInformation(View view) {
        String formattedRequest = "Request: " + busesRequested;
        sendSMS(TWILIO_NUMBER, formattedRequest);
    }

    private void sendSMS(String phoneNumber, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "\"" + msg  + "\" sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private ArrayList<Bus> getBuses(String input) {
        // At this stage, input can look like
        // GUILDFORD>8122:-122.842117,-122.842117|8123:-122.123456,123.123456|NEWTON EXCH>8123:122.80325,-122.80325|
        // Currently, we want to split it by destination
        // E.g, first match would return GUILDFORD>...
        // and second match would return NEWTON EXCH>...
        Pattern busPattern = Pattern.compile("([\\w\\s]*)>((\\d)+:-?(\\d)*\\.(\\d)+,-?(\\d)*\\.(\\d)+\\|)+");
        Matcher matcher = busPattern.matcher(input);

        ArrayList<Bus> listOfBuses = new ArrayList<>();
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

package com.kchen52.yetanothertranslinkapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BroadcastReceiver smsReceiver;

    // Used for formatting time/date for display
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");

    private ArrayList<KmlLayer> kmlLayers = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();

    private BusHandler busHandler;
    private RequestHandler requestHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        busHandler = new BusHandler(getApplicationContext());
        requestHandler = new RequestHandler(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Note: calling this code in onStart so changes made to the sharedpreferences in the settings or bus list screen are
        // immediately applied once coming back to the maps activity. onStart is also called after onCreate, so this also covers
        // that case lol
        // Read from SharedPreferences, and update TWILIO_NUMBER and busesRequested
        requestHandler.update();
        // When the user chooses a new route in the bus list menu and returns to the main activity, draw that route
        if (mMap != null) {
            //createRouteOverlays(busesRequested);
            createRouteOverlays(requestHandler.getBusesRequested());
        }
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
                Intent busListIntent = new Intent(this, BusListActivity.class);
                startActivity(busListIntent);
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // TODO: Allow user to dynamically set default gps location
        LatLng defaultMapLocation = new LatLng(49.118641, -122.747700);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultMapLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultMapLocation, 12.0f));

        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SMSReceiver();
        registerReceiver(smsReceiver, smsFilter);

        busHandler.updateWithLastText();
        drawBuses(busHandler.getBuses());
        updateDisplayedTime(busHandler.getLastUpdatedTime());

        createRouteOverlays(requestHandler.getBusesRequested());
    }

    private void createRouteOverlays(String busesRequested) {
        if (!busesRequested.equals("")) {
            // Clear the previous layers
            for (KmlLayer kmlLayer : kmlLayers) {
                kmlLayer.removeLayerFromMap();
            }
            kmlLayers.clear();

            // Then create new layers and draw them
            for (String bus : busesRequested.split(", ")) {
                KmlLayer kmlLayer;
                try {
                    int id = getResources().getIdentifier("raw/_" + bus, null, this.getPackageName());
                    kmlLayer = new KmlLayer(mMap, id, getApplicationContext());
                    kmlLayer.addLayerToMap();
                    kmlLayers.add(kmlLayer);
                    Log.d("OVERLAY_TEST", "Just added a new layer for " + bus + ", size(): " + kmlLayers.size());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

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
                        if (phoneNumber.equals(requestHandler.getTwilioNumber())) {
                            String message = currentMessage.getDisplayMessageBody();
                            entireSMS.append(message);
                        }
                    }
                    updateDisplayedTime(new Date());

                    busHandler.updateBuses(entireSMS.toString());
                    drawBuses(busHandler.getBuses());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDisplayedTime(Date currentTime) {
        String lastRequestedTime = dateFormat.format(currentTime);
        TextView lastRequestedTime_TextView= (TextView)findViewById(R.id.lastRequestDateTextView);
        String formattedTime = String.format(getResources().getString(R.string.last_updated_preamble), lastRequestedTime);
        lastRequestedTime_TextView.setText(formattedTime);
    }

    private void drawBuses(LinkedList<Bus> buses) {
        // If there are new buses, wipe the old ones from the map
        if (buses.size() != 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }
        for (Bus bus : buses) {
            Log.i("Drawing the following:", "Destination: " + bus.getDestination() + ", VehicleNo: " + bus.getVehicleNumber() +
                    ", Longitude: " + bus.getLongitude() + ", Latitude: " + bus.getLatitude());
            addMarker(bus);
        }
    }

    private void addMarker(Bus bus) {
        LatLng busLocation = new LatLng(bus.getLatitude(), bus.getLongitude());
        MarkerOptions temp = new MarkerOptions().position(busLocation)
                .title(bus.getDestination()+  ": " + bus.getVehicleNumber())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));
        markers.add(mMap.addMarker(temp));
    }

    public void requestInformation(View view) {
        // Create a snackbar to let the user know what was requested, if at all
        String busesRequested = requestHandler.getBusesRequested();
        if (busesRequested.equals("")) {
            Snackbar.make(view, "No request was sent because no buses are selected.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(view, "Information request for " + busesRequested + " sent.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            String formattedRequest = "Request: " + busesRequested;
            requestHandler.sendSMS(formattedRequest);
        }
    }
}

package com.kchen52.yetanothertranslinkapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;
import com.kchen52.yetanothertranslinkapp.handlers.BusHandler;
import com.kchen52.yetanothertranslinkapp.handlers.HandlerExtension;
import com.kchen52.yetanothertranslinkapp.handlers.RequestHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private final String TAG = "MapsActivity";
    private HandlerExtension handlerExtension;

    private GoogleMap mMap;
    private int MY_LOCATION_REQUEST_CODE = 1;

    // Used for formatting time/date for display
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");

    private ArrayList<Marker> markers = new ArrayList<>();

    private RequestHandler requestHandler;

    private SharedPreferences sharedPref;

    @Override
    protected void onPause() {
        if (mMap != null) {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            LatLng latLng = cameraPosition.target;
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putFloat("lastLat", (float) latLng.latitude);
            sharedPrefEditor.putFloat("lastLong", (float) latLng.longitude);
            sharedPrefEditor.putFloat("lastZoom", (float) cameraPosition.zoom);
            sharedPrefEditor.commit();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        handlerExtension = new HandlerExtension(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        requestHandler = new RequestHandler(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Note: calling this code in onStart so changes made to the sharedpreferences in the settings or bus list screen are
        // immediately applied once coming back to the maps activity. onStart is also called after onCreate, so this also covers
        // that case lol
        requestHandler.update();
        // When the user chooses a new route in the bus list menu and returns to the main activity, draw that route
        if (mMap != null) {
            createRouteOverlays(requestHandler.getBusesRequested());
            centerCamera(
                    (double) sharedPref.getFloat("lastLat", 49.264566F),
                    (double) sharedPref.getFloat("lastLong", -123.133253F),
                    sharedPref.getFloat("lastZoom", 10f));
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
        CameraPosition cameraPosition = mMap.getCameraPosition();
        LatLng latLng = cameraPosition.target;
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putFloat("lastLat", (float) latLng.latitude);
        sharedPrefEditor.putFloat("lastLong", (float) latLng.longitude);
        sharedPrefEditor.putFloat("lastZoom", cameraPosition.zoom);
        sharedPrefEditor.commit();
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

        centerCamera(
                (double) sharedPref.getFloat("lastLat", 49.264566F),
                (double) sharedPref.getFloat("lastLong", -123.133253F),
                sharedPref.getFloat("lastZoom", 10f));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_LOCATION_REQUEST_CODE);
        }
        createRouteOverlays(requestHandler.getBusesRequested());
    }

    private void createRouteOverlays(String busesRequested) {
        if ("".equals(busesRequested)) {
            return;
        }
        // Clear the entire map
        mMap.clear();
        // Then create new layers and draw them
        for (String bus : busesRequested.split(", ")) {
            KmlLayer kmlLayer;
            try {
                int id = getResources().getIdentifier("raw/_" + bus.toLowerCase(), null, this.getPackageName());
                kmlLayer = new KmlLayer(mMap, id, getApplicationContext());
                kmlLayer.addLayerToMap();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void updateDisplayedTime(Date currentTime) {
        String formattedTime;
        if (currentTime == null) {
            formattedTime = "Last updated: Never";
        } else {
            String lastRequestedTime = dateFormat.format(currentTime);
            formattedTime = String.format(getResources().getString(R.string.last_updated_preamble), lastRequestedTime);
        }
        TextView lastRequestedTime_TextView = (TextView) findViewById(R.id.lastRequestDateTextView);
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

    private void centerCamera(double avgLat, double avgLong, float zoomLevel) {
        LatLng newLocation = new LatLng(avgLat, avgLong);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoomLevel));
    }

    private void addMarker(Bus bus) {
        LatLng busLocation = new LatLng(bus.getLatitude(), bus.getLongitude());
        MarkerOptions temp = new MarkerOptions().position(busLocation)
                .title(bus.getDestination()+  ": " + bus.getVehicleNumber());

        if ("west".equals(bus.getDirection().toLowerCase())) {
            temp.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp_west));
        } else if ("east".equals(bus.getDirection().toLowerCase())) {
            temp.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp_east));
        } else {
            // And in the case it's a direction I can't parse, just show the default bus icon
            temp.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));

        }
        markers.add(mMap.addMarker(temp));
    }

    /*
     * Displays a snackbar indicated what was done, then calls the action in RequestHandler
     */
    public void requestInformation(View view) {
        String busesRequested = requestHandler.getBusesRequested();
        if (busesRequested.equals("")) {
            Snackbar.make(view, "Please select at least one bus route to track.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        if (!requestHandler.hasActiveInternetConnection()) {
            Snackbar.make(view, "This device currently doesn't have an internet connection. " +
                    "Please try again later.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        requestHandler.updateWithInternet(handlerExtension);
    }

    // Takes the message from the Handler and updates the UI
    public void updateDisplayedTimeAndDrawBuses(Message message) {
        Bundle bundle = message.getData();
        BusHandler busHandler = bundle.getParcelable("busHandler");
        updateDisplayedTime(busHandler.getLastUpdatedTime());
        drawBuses(busHandler.getBuses());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) throws SecurityException {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }
}

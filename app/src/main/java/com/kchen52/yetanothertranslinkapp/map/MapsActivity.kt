package com.kchen52.yetanothertranslinkapp.map

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kchen52.yetanothertranslinkapp.BusListActivity
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.SettingsActivity
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    MapsActivityView {
    private var googleMap: GoogleMap? = null
    private val MY_LOCATION_REQUEST_CODE = 1

    private val viewModel = MapsActivityViewModel()

    private var sharedPref: SharedPreferences? = null
    override fun onPause() {
        if (googleMap != null) {
            val cameraPosition = googleMap!!.cameraPosition
            val latLng = cameraPosition.target
            val sharedPrefEditor = sharedPref!!.edit()
            sharedPrefEditor.putFloat("lastLat", latLng.latitude.toFloat())
            sharedPrefEditor.putFloat("lastLong", latLng.longitude.toFloat())
            sharedPrefEditor.putFloat("lastZoom", cameraPosition.zoom)
            sharedPrefEditor.apply()
        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        viewModel.state.subscribe { mapsActivityState ->
            render(mapsActivityState)
        }

        refreshBusesButton.setOnClickListener {
            viewModel.onIntent(MapsActivityIntents.LoadBuses)
        }
    }

    override fun onStart() {
        super.onStart()
        // When the user chooses a new route in the bus list menu and returns to the main activity, draw that route
        googleMap?.let {
            centerCamera(
                sharedPref?.getFloat("lastLat", DEFAULT_LATITUDE_VALUE)?.toDouble() ?: DEFAULT_LATITUDE_VALUE.toDouble(),
                sharedPref?.getFloat("lastLong", DEFAULT_LONGITUDE_VALUE)?.toDouble() ?: DEFAULT_LONGITUDE_VALUE.toDouble(),
                sharedPref?.getFloat("lastZoom", DEFAULT_ZOOM_LEVEL) ?: DEFAULT_ZOOM_LEVEL)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.maps_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val cameraPosition = googleMap!!.cameraPosition
        val latLng = cameraPosition.target
        val sharedPrefEditor = sharedPref!!.edit()
        sharedPrefEditor.putFloat("lastLat", latLng.latitude.toFloat())
        sharedPrefEditor.putFloat("lastLong", latLng.longitude.toFloat())
        sharedPrefEditor.putFloat("lastZoom", cameraPosition.zoom)
        sharedPrefEditor.apply()
        return when (item.itemId) {
            R.id.bus_list -> {
                val busListIntent = Intent(this, BusListActivity::class.java)
                startActivity(busListIntent)
                true
            }
            R.id.settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        centerCamera(
            sharedPref!!.getFloat("lastLat", DEFAULT_LATITUDE_VALUE).toDouble(),
            sharedPref!!.getFloat("lastLong", DEFAULT_LONGITUDE_VALUE).toDouble(),
            sharedPref!!.getFloat("lastZoom", DEFAULT_ZOOM_LEVEL))
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            this.googleMap!!.isMyLocationEnabled = true
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), MY_LOCATION_REQUEST_CODE)
        }
    }

    private fun centerCamera(avgLat: Double, avgLong: Double, zoomLevel: Float) {
        val newLocation = LatLng(avgLat, avgLong)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoomLevel))
    }

    @Throws(SecurityException::class)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.size == 1 && permissions[0] === Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap!!.isMyLocationEnabled = true
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }


    override fun render(state: MapsActivityState) {
        runOnUiThread {
            when (state) {
                is MapsActivityState.DataState -> {
                    renderDataState(state)
                }
                is MapsActivityState.LoadingState -> {
                    renderLoading()
                }
                is MapsActivityState.ErrorState -> {
                    renderError(state.exception)
                }
            }
        }
    }

    fun renderDataState(dataState: MapsActivityState.DataState) {
        // Remove all existing markers first
        googleMap?.clear()

        loadingBusesProgressBar.visibility = View.GONE
        for (bus in dataState.buses) {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(bus.latitude, bus.longitude))
                    .title("${bus.destination} : ${bus.vehicleNo}")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp))
            )
        }
        val lastRequestTime = "Last requested: ${dataState.timeUpdated}"
        lastRequestDateTextView.text = lastRequestTime
    }

    fun renderLoading() {
        loadingBusesProgressBar.visibility = View.VISIBLE
    }

    fun renderError(exception: Exception) {
        loadingBusesProgressBar.visibility = View.GONE
        // Make a toast or snackbar?
    }

    companion object {
        private val DEFAULT_ZOOM_LEVEL = 10f
        private val DEFAULT_LATITUDE_VALUE = 49.264566f
        private val DEFAULT_LONGITUDE_VALUE = -123.133253f
        private val TAG = "MapsActivity"
    }
}
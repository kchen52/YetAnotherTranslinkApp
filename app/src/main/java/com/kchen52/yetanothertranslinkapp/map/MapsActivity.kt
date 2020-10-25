package com.kchen52.yetanothertranslinkapp.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
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
import com.kchen52.yetanothertranslinkapp.buslist.BusListActivity
import com.kchen52.yetanothertranslinkapp.R
import com.kchen52.yetanothertranslinkapp.SettingsActivity
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsActivityView {
    private var googleMap: GoogleMap? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private val viewModel: MapsActivityViewModel by viewModels { MapsActivityViewModelFactory(applicationContext, coroutineScope) }

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        sharedPref = getSharedPreferences(MapConstants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)


        coroutineScope.launch {
            viewModel.getMapsActivityState().collect { mapsActivityState ->
                withContext(Dispatchers.Main) {
                    render(mapsActivityState)
                }
            }
        }

        refreshBusesButton.setOnClickListener {
            // Read buses from shared prefs
            val requestedBuses = sharedPref.getString("requestedBuses", "")?.map { it.toInt() }?.toIntArray() ?: intArrayOf()
            viewModel.onIntent(MapsActivityIntents.LoadBuses(requestedBuses))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.maps_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            this.googleMap?.isMyLocationEnabled = true
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), MY_LOCATION_REQUEST_CODE)
        }

        centerCamera(
            lat = sharedPref.getFloat("lastLat", MapConstants.DEFAULT_LATITUDE_VALUE),
            long = sharedPref.getFloat("lastLong", MapConstants.DEFAULT_LONGITUDE_VALUE),
            zoomLevel = sharedPref.getFloat("lastZoom", MapConstants.DEFAULT_ZOOM_LEVEL)
        )
    }


    @Throws(SecurityException::class)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.size == 1 && permissions[0] === Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap?.isMyLocationEnabled = true
            } else {
                // TODO: Determine what should actually be done here. Display an error message?
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

    private fun renderDataState(dataState: MapsActivityState.DataState) {
        // Unfortunately there's nothing we can do here :|
        // TODO: Can we await the map being ready?
        if (googleMap == null) {
            return
        }

        // Remove all existing markers first
        googleMap?.clear()

        // Only perform a camera shift if we have nonnull values for long, lat, and zoom
        if (dataState.latitude != null && dataState.longitude != null && dataState.zoom != null) {
            centerCamera(dataState.latitude, dataState.longitude, dataState.zoom)
        }

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

    private fun renderLoading() {
        loadingBusesProgressBar.visibility = View.VISIBLE
    }

    private fun renderError(exception: Exception) {
        loadingBusesProgressBar.visibility = View.GONE
        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
    }

    // Centres the Google Map object on the provided latitude, longitude, and at the zoom level
    // provided.
    private fun centerCamera(lat: Float, long: Float, zoomLevel: Float) {
        googleMap?.run {
            val newLocation = LatLng(lat.toDouble(), long.toDouble())
            moveCamera(CameraUpdateFactory.newLatLng(newLocation))
            animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoomLevel))
        }
    }

    // Save the current camera position so we go back to it the next time we resume
    override fun onPause() {
        super.onPause()
        googleMap?.let { googleMap ->
            val cameraPosition = googleMap.cameraPosition
            val latLng = cameraPosition.target
            val sharedPrefEditor = sharedPref.edit()
            sharedPrefEditor.putFloat("lastLat", latLng.latitude.toFloat())
            sharedPrefEditor.putFloat("lastLong", latLng.longitude.toFloat())
            sharedPrefEditor.putFloat("lastZoom", cameraPosition.zoom)
            sharedPrefEditor.apply()
        }
    }


    companion object {
        private const val TAG = "MapsActivity"
        private const val MY_LOCATION_REQUEST_CODE = 1
    }
}
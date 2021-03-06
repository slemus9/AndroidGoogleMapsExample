package com.example.googlemapsexample

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.googlemapsexample.data.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Based on the Google Maps Platform documentation and examples:
 * https://developers.google.com/maps/documentation/android-sdk/overview
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val TAG = MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
    }

    private var map: GoogleMap? = null

    private lateinit var client: FusedLocationProviderClient

    private var permissionGranted = false

    private var lastKnownLocation: Location? = null

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this).get(MapsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        client = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.currentLocation.observe(this, {
            goToCurrentLocation(it)
        })

        viewModel.nearbyPlaces.observe(this) {
            renderNearbyHospitals(it)
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (permissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (permissionGranted) {
                val locationResult = client.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        Log.i(TAG, "Current location ${task.result}")
                        if (lastKnownLocation != null) {
                            val currLocation = LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            )
                            viewModel.updateCurrentLocation(currLocation)
                            //val currLocation = viewModel.currentLocation.value!!
//                            map?.addMarker(MarkerOptions().position(currLocation).title("You are here"))
//                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    currLocation,
//                                    DEFAULT_ZOOM.toFloat()
//                            ))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        viewModel.updateCurrentLocation(defaultLocation)
                        map?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        //map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun goToCurrentLocation (location: LatLng) {
        map?.addMarker(MarkerOptions().position(location).title("You are here"))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
            location,
            DEFAULT_ZOOM.toFloat()
        ))
    }

    private fun renderNearbyHospitals (nearby: List<Place>) {
        nearby.forEach { place ->
            val lat = place.geometry.location.lat
            val lng = place.geometry.location.lng
            val pos = LatLng(lat, lng)
            Log.i(TAG, "Place: (${pos.latitude}, ${pos.longitude})")
            map?.addMarker(MarkerOptions().position(pos).title(place.name))
//                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    pos,
//                    DEFAULT_ZOOM.toFloat()
//                ))
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()

        //val currentLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
//        val currentLocation = defaultLocation
//        // Add a marker in Sydney and move the camera
//        map?.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
//        map?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        permissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

}
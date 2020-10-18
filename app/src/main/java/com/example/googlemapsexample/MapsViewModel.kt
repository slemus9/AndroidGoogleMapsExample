package com.example.googlemapsexample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.googlemapsexample.data.NearbyPlacesResponse
import com.example.googlemapsexample.data.Place
import com.example.googlemapsexample.services.LocationApi
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {
    private val _nearbyPlaces = MutableLiveData<List<Place>>()
    val nearbyPlaces: LiveData<List<Place>>
        get () = _nearbyPlaces

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng>
        get () = _currentLocation

    init {
        _currentLocation.value = LatLng(-33.8523341, 151.2106085)
        getNearbyPharmacies()
    }

    fun getNearbyPharmacies () {
        val location = "${currentLocation.value?.latitude},${currentLocation.value?.longitude}"
        Log.i("NearbyPlacesViewModel", location)
        LocationApi.retrofitService.getNearbyPharmacies(location, 5000).enqueue(object:
            Callback<NearbyPlacesResponse> {
            override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                Log.e("NearbyPlacesViewModel", t.message!!)
            }

            override fun onResponse(call: Call<NearbyPlacesResponse>, response: Response<NearbyPlacesResponse>) {
                Log.i("NearbyPlacesViewModel", call.request().url().toString())
                Log.i("NearbyPlacesViewModel", response.body()?.results?.size.toString())
                _nearbyPlaces.value = response.body()?.results
            }
        })
    }

    fun updateCurrentLocation (location: LatLng) {
        _currentLocation.value = location
        getNearbyPharmacies()
    }
}
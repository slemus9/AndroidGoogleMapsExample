package com.example.googlemapsexample.services

import com.example.googlemapsexample.BuildConfig
import com.example.googlemapsexample.data.NearbyPlacesResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"

private const val KEY = BuildConfig.MAPS_API_KEY

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface LocationApiService  {
    @GET("json?types=pharmacy&key=${KEY}")
    fun getNearbyPharmacies (
        @Query("location") location: String,
        @Query("radius") radius: Int,
    ) : Call<NearbyPlacesResponse>
}

object LocationApi {
    val retrofitService: LocationApiService by lazy {
        retrofit.create(LocationApiService::class.java)
    }
}
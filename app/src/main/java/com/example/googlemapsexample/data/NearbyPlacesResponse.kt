package com.example.googlemapsexample.data

import com.squareup.moshi.Json

data class NearbyPlacesResponse(
    @Json(name = "html_attributions") @Transient var htmlAttributions: List<String> = listOf(""),
    @Json(name = "next_page_token") var nextPageToken: String = "",
    @Json(name = "results") val results: List<Place> = listOf(),
    var status: String = ""
)
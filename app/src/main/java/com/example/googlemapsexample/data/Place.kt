package com.example.googlemapsexample.data

import com.squareup.moshi.Json

data class Place(
    @Json(name = "business_status") var businessStatus: String = "",
    val geometry: Geometry,
    var icon: String = "",
    var name: String = "",
    @Json(name = "place_id") var placeId: String = "",
    @Json(name = "plus_code") val plusCode: PlusCode,
    var reference: String = "",
    var scope: String = "",
    val types: List<String> = listOf(""),
    var vicinity: String = ""
)

data class Geometry (
    val location: Location,
    @Json(name = "viewport") val viewPort: ViewPort
)

data class ViewPort (
    val northeast: Location,
    val southwest: Location
)

data class Location (
    var lat: Double = 0.0,
    var lng: Double = 0.0
)

data class PlusCode (
    @Json(name = "compound_code") var compoundCode: String = "",
    @Json(name = "global_code") var globalCode: String = ""
)
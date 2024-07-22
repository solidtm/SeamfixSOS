package com.solid.seamfixsos.data.model

object Dto {

    data class SosRequest(
        val phoneNumbers: List<String>,
        val image: String,
        val location: Location
    )

    data class SosResponse(
        val data: Data,
        val message: String,
        val status: String
    )

    data class Data(
        val id: Int,
        val image: String,
        val location: Location,
        val phoneNumbers: List<String>
    )

    data class Location(
        val latitude: String,
        val longitude: String
    )
}
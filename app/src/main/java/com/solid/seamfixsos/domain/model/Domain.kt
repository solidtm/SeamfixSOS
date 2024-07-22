package com.solid.seamfixsos.domain.model

object Domain {

    data class SosResponse(
        val message: String,
        val status: String
    )

    data class LatLong(
        var lat: Double?,
        var long: Double?
    )

    data class GenericResponse(
        val success: Boolean?,
        val statusCode: Int?,
        val message: String?,
    )
}
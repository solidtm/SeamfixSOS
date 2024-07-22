package com.solid.seamfixsos.features.util

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.CountDownTimer
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.solid.seamfixsos.domain.model.Domain
import java.util.Locale

class LocationHelper (private val context: Context) :
    ContextWrapper(context) {
    private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    private val settingsClient by lazy { LocationServices.getSettingsClient(this) }

    private val locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setFastestInterval(MIN_UPDATE) // Every 1sec for location resource sharing
        .setInterval(MAX_UPDATE) // Get Location every 3 seconds

    private val lastLocationRequest by lazy {
        LastLocationRequest.Builder().apply {
            setGranularity(Granularity.GRANULARITY_FINE)
            setMaxUpdateAgeMillis(MAX_UPDATE)
        }.build()
    }

    private val locationSettingsRequest by lazy {
        LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
    }

    @RequiresPermission(allOf = [Permissions.ACCESS_COARSE_LOCATION, Permissions.ACCESS_BACKGROUND_LOCATION, Permissions.ACCESS_FINE_LOCATION])
    fun getCurrentLocation(callback: (latLng: Domain.LatLong?, address: String?) -> Unit) {

        // Set a timer that waits for 5 seconds to get location update
        val timer = object : CountDownTimer(10 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                callback(null, NOT_FOUND)
            }
        }
        timer.start()

        fusedLocationProviderClient.getLastLocation(lastLocationRequest)
            .addOnSuccessListener { location ->
                if (location != null) {
                    timer.cancel()
                    val latLng = Domain.LatLong(location.latitude, location.longitude)
                    val address = getAddress(location.latitude, location.longitude)
                    callback(latLng, address)
                }
            }
    }

    private fun getAddress(lat: Double, lng: Double): String? {
        return try {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(lat, lng, 1)
            addresses?.get(0)?.getAddressLine(0)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    fun isGpsEnabled() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    fun onLocationSettingsSuccessListener(onSuccess: () -> Unit): Task<LocationSettingsResponse> {
        return settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener { onSuccess() }
    }

    private val locationCallback = object : LocationCallback() {
        private var resultCallback: (() -> Unit)? = null
        fun setCallBack(callback: () -> Unit) {
            this.resultCallback = callback
        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            // Wait for some time (5 sec) to get better location updates
            val timer = object : CountDownTimer(10 * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    resultCallback?.invoke()
                }
            }
            timer.start()
        }

        fun resetCallback() {
            resultCallback = null
        }

    }

    fun resetLocationCallback() {
        locationCallback.resetCallback()
    }

    fun requestLocationUpdate(callback: (() -> Unit)? = null) {
        val permissions =
            arrayOf(Permissions.ACCESS_COARSE_LOCATION, Permissions.ACCESS_FINE_LOCATION)
        val permissionsGranted = permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (callback != null) locationCallback.setCallBack(callback)
        if (permissionsGranted) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        const val NOT_FOUND = "Location not found"
        private const val MAX_UPDATE = 20_000L
        private const val MIN_UPDATE = 10_000L
    }
}
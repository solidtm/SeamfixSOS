package com.solid.seamfixsos.features.sos

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.solid.seamfixsos.data.model.Dto
import com.solid.seamfixsos.databinding.ActivitySosBinding
import com.solid.seamfixsos.domain.functional.Result
import com.solid.seamfixsos.domain.model.Domain
import com.solid.seamfixsos.features.util.LocationHelper
import com.solid.seamfixsos.features.util.Permissions
import com.solid.seamfixsos.features.util.compressImage
import com.solid.seamfixsos.features.util.gone
import com.solid.seamfixsos.features.util.isPermissionsGranted
import com.solid.seamfixsos.features.util.resizeBitmap
import com.solid.seamfixsos.features.util.showDialog
import com.solid.seamfixsos.features.util.visibleIf
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File
import java.io.FileInputStream
import java.util.Date

class SosActivity : AppCompatActivity() {
    private lateinit var imageFile: File
    private var currentPhotoPath = ""
    private lateinit var imageString: String
    private lateinit var latLong: Domain.LatLong
    private lateinit var locationHelper: LocationHelper
    private lateinit var binding: ActivitySosBinding
    private lateinit var viewModel: SosViewModel
    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            val somePermissionsNotGranted = permissions.any {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }
            if (somePermissionsNotGranted) {
                this.showDialog(
                    "Permission required",
                    message = "You need to accept all permissions for us to get your location",
                    negativeTitle = "Cancel",
                    positiveTitle = "Continue"
                ) {}
            } else {
                getCurrentLocation()
            }
        }
    private val imageResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.entries.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it.key
                ) == PackageManager.PERMISSION_GRANTED
            }

            Toast.makeText(this, "$granted", Toast.LENGTH_LONG).show()
            if (granted) {
                takeImage()
            } else {
                this.showDialog(message = "Please accept all permissions to continue")
            }
        }
    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)

                if (imageBitmap != null) {
                    val resizedBitmap = resizeBitmap(imageBitmap, 1080, 1920) // Resize to reasonable dimensions
                    val compressedImageFile = compressImage(resizedBitmap, 4 * 1024 * 1024, this) // Limit to 4MB

                    // Convert the compressed to Base64 string
                    binding.image.visibleIf(true)
                    binding.image.setImageBitmap(imageBitmap)
                    val base64String = convertImageFileToBase64(compressedImageFile)
                    imageString = "base64 String===$base64String"
                }
            }
        }

    companion object {
        private const val LOCATION_RESOLUTION_RC = 209
        private val permissions = arrayOf(Permissions.ACCESS_COARSE_LOCATION, Permissions.ACCESS_FINE_LOCATION)
        private val imagePermissions = arrayOf(Permissions.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationHelper = LocationHelper(this)
        viewModel = getViewModel()
        setUpClicks()
    }

    private fun setUpClicks() {
        with(binding) {
            sosBtn.setOnClickListener { onPickImageClicked() }
            locationBtn.setOnClickListener { getCurrentLocation() }
            submitBtn.setOnClickListener {
                if (::imageString.isInitialized && locationText.text.equals("Location captured")) {
                    val request = Dto.SosRequest(
                        image = imageString,
                        location = Dto.Location(
                            longitude = latLong.long.toString(),
                            latitude = latLong.lat.toString()
                        ),
                        phoneNumbers = arrayListOf("080333333333", "080444444444")
                    )
                    viewModel.createSos(request)
                    lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.sosResponse.collect { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        binding.progressIndicatorSubmit.visibleIf(true)
                                    }

                                    is Result.Success -> {
                                        binding.progressIndicatorSubmit.gone()
                                        this@SosActivity.showDialog(
                                            title = "Success",
                                            message = result.data?.message
                                                ?: "Sos recorded successfully",
                                        ){finish()}
                                    }

                                    is Result.Error -> {
                                        binding.progressIndicatorSubmit.gone()
                                        this@SosActivity.showDialog(
                                            title = "Error",
                                            message = result.message
                                                ?: "An error occurred, please try again.",
                                        ){finish()}
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@SosActivity,
                        "Please capture image and location to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun takeImage() {
        val directoryName = "sos"
        val directory = File(
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            directoryName
        )

        if (!directory.exists()) {
            directory.mkdir()
        }

        imageFile = File.createTempFile("image" + Date().time, ".png", directory)
        val imageUri = FileProvider.getUriForFile(
            this,
            "com.solid.seamfixsos.provider",
            imageFile
        )
        currentPhotoPath = imageFile.absolutePath
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        takeImageLauncher.launch(intent)
    }

    private fun requestImagePermissions() {
        imageResultLauncher.launch(imagePermissions)
    }

    private fun requestNeededPermissions() {
        locationPermissionRequest.launch(permissions)
    }

    private fun onPickImageClicked() {
        val granted = this.isPermissionsGranted(*imagePermissions)

        if (!granted) {
            requestImagePermissions()
            return
        }

        takeImage()
    }

    private fun getCurrentLocation() {
        val somePermissionsNotGranted = permissions.any {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (somePermissionsNotGranted) {
            this.showDialog(
                "Permission required",
                message = "Please accept all permissions to continue",
                negativeTitle = "Cancel",
                positiveTitle = "Continue"
            ) {
                requestNeededPermissions()
            }
            return
        }

        if (!locationHelper.isGpsEnabled()) {
            locationHelper.onLocationSettingsSuccessListener { getLocation() }
                .addOnFailureListener { doLocationResolution(it) }
        } else {
            getLocation()
        }
    }

    @RequiresPermission(allOf = [Permissions.ACCESS_COARSE_LOCATION, Permissions.ACCESS_BACKGROUND_LOCATION, Permissions.ACCESS_FINE_LOCATION])
    private fun getLocation() {
        binding.progressIndication.visibleIf(true)

        locationHelper.requestLocationUpdate {

            locationHelper.getCurrentLocation { latLng, address ->
                val locationAddress = "Location captured"

                if (latLng != null) {
                    latLong = latLng
                    binding.locationText.text = locationAddress
                } else {
                    binding.locationText.text = "Capture failed"
                }

                locationHelper.resetLocationCallback()
                // Remove location callback on getting location
                locationHelper.stopLocationUpdate()

                //Hide Loaders
                binding.progressIndication.gone()
            }
        }
    }

    private fun doLocationResolution(exception: Exception) {
        if (exception is ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        val rae = exception as? ResolvableApiException
                        this.apply {
                            rae?.startResolutionForResult(this, LOCATION_RESOLUTION_RC)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        println("Location settings error $ex")
                    }
                }
            }
        }
    }

    private fun convertImageFileToBase64(file: File): String {
        val fileInputStream = FileInputStream(file)
        val byteArray = fileInputStream.readBytes()
        fileInputStream.close()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
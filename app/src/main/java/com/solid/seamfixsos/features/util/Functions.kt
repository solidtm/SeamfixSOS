package com.solid.seamfixsos.features.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.View
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val ratioBitmap = width.toFloat() / height.toFloat()

    val finalWidth: Int
    val finalHeight: Int

    if (width > height) {
        finalWidth = maxWidth
        finalHeight = (finalWidth / ratioBitmap).toInt()
    } else {
        finalHeight = maxHeight
        finalWidth = (finalHeight * ratioBitmap).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
}

fun compressImage(image: Bitmap, maxSize: Int, context: Context): File {
    val outputStream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // You can adjust the compression quality here
    var quality = 80
    while (outputStream.toByteArray().size > maxSize && quality > 0) {
        outputStream.reset()
        image.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        quality -= 10
    }

    val compressedImageFile = File.createTempFile("compressed_image", ".jpg", context.cacheDir)
    val fileOutputStream = FileOutputStream(compressedImageFile)
    fileOutputStream.write(outputStream.toByteArray())
    fileOutputStream.flush()
    fileOutputStream.close()

    return compressedImageFile
}

fun Context.showDialog(
    title: String = "Error",
    message: String,
    positiveTitle: String = "ok",
    negativeTitle: String? = "",
    dismissible: Boolean = false,
    negativeCallback: (() -> Unit)? = null,
    positiveCallback: (() -> Unit)? = null
) {

    MaterialDialog(this).show {
        cornerRadius(5F)
        title(text = title)
        message(text = message)
        cancelable(dismissible)

        positiveButton(text = positiveTitle) { _ ->
            positiveCallback?.invoke()
        }

        negativeButton(text = negativeTitle) {
            negativeCallback?.invoke()
            dismiss()
        }
    }
}

fun View.visibleIf(show: Boolean?) {
    visibility = if (show == true) View.VISIBLE
    else View.GONE
}

fun View.gone() {
    visibility = View.GONE
}
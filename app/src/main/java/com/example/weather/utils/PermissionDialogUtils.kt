package com.example.weather.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

//Singleton which stores alert dialogs for some missing permissions
object PermissionDialogUtils {

    //Dialog to show if some permissions for app are disabled
    fun Activity.showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setTitle("Missing Permission")
            .setMessage("You turned off permissions required. Please Enable them!")
            .setPositiveButton("Settings") { _, _ ->
                try {
                    //Open settings on the phone to provide location permissions to the app
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //Dialog to show if Location Service (GPS) is OFF
    fun Activity.showLocationNotEnabled() {
        AlertDialog.Builder(this)
            .setTitle("Location Disabled")
            .setMessage("Your location provider is turned OFF, turn it ON.")
            .setPositiveButton("Settings") { _, _ ->
                try {
                    //Open setting on the phone to enable GPS
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}
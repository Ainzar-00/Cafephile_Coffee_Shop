package com.example.f053

import android.Manifest
import android.app.Activity
import pub.devrel.easypermissions.EasyPermissions

class LocationPermission(private val activity: Activity) {

    fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun requestPermission() {
        EasyPermissions.requestPermissions(
            activity,
            activity.getString(R.string.location_permission_msg),
            LOCATION_PERMISSION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

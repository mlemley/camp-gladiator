package app.camp.gladiator.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


interface IPermission {
    val permission:String
}

sealed class Permission: IPermission {

    data class LocationPermission(
        override val permission: String = Manifest.permission.ACCESS_FINE_LOCATION
    ) : Permission()
}

class PermissionUtil constructor(private val context: Context) {

    fun hasPermissionFor(permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(context, permission.permission) == PackageManager.PERMISSION_GRANTED
    }
}
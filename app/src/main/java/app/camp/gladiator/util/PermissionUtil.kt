package app.camp.gladiator.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


interface IPermission {
    val name:String
}

sealed class Permission: IPermission {

    data class LocationPermission(
        override val name: String = Manifest.permission.ACCESS_FINE_LOCATION
    ) : Permission()
}

class PermissionUtil constructor(private val context: Context) {

    fun hasPermissionFor(permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(context, permission.name) == PackageManager.PERMISSION_GRANTED
    }

    fun foo() {
    }
}

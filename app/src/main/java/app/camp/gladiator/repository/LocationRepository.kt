package app.camp.gladiator.repository

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LocationRepository(
    private val locationManager: LocationManager,
    private val permissionRepository: PermissionRepository
) {
    @SuppressLint("MissingPermission")
    suspend fun lastKnownLocation(): Location = withContext(Dispatchers.IO) {
        if (permissionRepository.hasPermissionFor(Permission.LocationPermission()))
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: emptyLocation()
        else emptyLocation()
    }

    private fun emptyLocation(): Location = Location(LocationManager.PASSIVE_PROVIDER)

}
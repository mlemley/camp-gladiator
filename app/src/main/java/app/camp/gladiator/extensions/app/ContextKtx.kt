package app.camp.gladiator.extensions.app

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


val Context.locationServices: FusedLocationProviderClient
    get() = LocationServices.getFusedLocationProviderClient(this)

val Context.locationManager: LocationManager
    get() = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

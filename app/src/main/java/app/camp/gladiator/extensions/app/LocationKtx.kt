package app.camp.gladiator.extensions.app

import android.location.Location
import com.google.android.gms.maps.model.LatLng


fun Location.toLatLng():LatLng = LatLng(latitude, longitude)
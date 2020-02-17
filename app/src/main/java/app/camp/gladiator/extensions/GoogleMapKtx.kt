package app.camp.gladiator.extensions

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


fun GoogleMap.moveCameraTo(latLng: LatLng, zoomLevel: Float = 12F) =
    this.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

fun GoogleMap.plot(latLng: LatLng, plotTitle: String? = null) = this.addMarker(
    MarkerOptions().position(latLng).also { options ->
        plotTitle?.let { markerTitle ->
            options.title(markerTitle)
        }
    })

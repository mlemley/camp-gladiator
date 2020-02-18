package app.camp.gladiator.extensions

import app.camp.gladiator.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


fun GoogleMap.moveCameraTo(latLng: LatLng, zoomLevel: Float = 12F) =
    this.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))

fun GoogleMap.clearPins() = this.clear()

fun GoogleMap.plot(latLng: LatLng, plotTitle: String? = null) {
    this.addMarker(
        MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.cg_map_pin))
            .also { options ->
                plotTitle?.let { markerTitle ->
                    options.title(markerTitle)
                }
            })
}

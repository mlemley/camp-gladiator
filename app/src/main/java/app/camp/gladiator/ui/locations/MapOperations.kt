package app.camp.gladiator.ui.locations

import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.clearPins
import app.camp.gladiator.extensions.moveCameraTo
import app.camp.gladiator.extensions.plot
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng


interface MapOperation {
    fun operateWith(map: GoogleMap)
}

sealed class MapOperations : MapOperation {
    object EnableLocationRendering : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    class CenterOn(val focalPoint: LatLng) : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.moveCameraTo(focalPoint)
        }
    }

    class PlotLocations(val locations: List<TrainingLocation>) : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.clearPins()
            locations.forEach { location ->
                map.plot(location.toLatLng(), location.name)
            }
        }
    }

    class ObserveCameraMove(val onCameraMoveListener: GoogleMap.OnCameraMoveListener) :
        MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.setOnCameraMoveListener(onCameraMoveListener)
        }

    }
}

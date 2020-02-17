package app.camp.gladiator.ui.locations

import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.app.toLatLng
import app.camp.gladiator.extensions.moveCameraTo
import app.camp.gladiator.extensions.plot
import com.google.android.gms.maps.GoogleMap


interface MapOperation {
    fun operateWith(map: GoogleMap)
}

sealed class MapOperations : MapOperation {
    object EnableLocationRendering : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.isMyLocationEnabled = true
        }
    }

    class CenterOn(val location: Location) : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.moveCameraTo(location.toLatLng())
        }
    }

    class PlotLocations(val locations: List<TrainingLocation>) : MapOperations() {
        override fun operateWith(map: GoogleMap) {
            locations.forEach { location ->
                map.plot(location.toLatLng(), location.name)
            }
        }
    }

    class ObserveCameraMove(val onCameraMoveListener:GoogleMap.OnCameraMoveListener):MapOperations() {
        override fun operateWith(map: GoogleMap) {
            map.setOnCameraMoveListener(onCameraMoveListener)
        }

    }
}

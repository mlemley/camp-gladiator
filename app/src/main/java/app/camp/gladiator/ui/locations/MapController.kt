package app.camp.gladiator.ui.locations

import com.google.android.gms.maps.SupportMapFragment

class MapController {

    fun performMapOperation(fragment: SupportMapFragment, mapOperation: MapOperation) {
        fragment.getMapAsync { map ->
            mapOperation.operateWith(map)
        }
    }
}

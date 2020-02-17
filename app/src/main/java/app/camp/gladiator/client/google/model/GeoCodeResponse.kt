package app.camp.gladiator.client.google.model

import android.location.Location
import android.location.LocationManager
import com.google.gson.annotations.SerializedName


data class GeoCodeResponse(
    val results: List<GeoCodeResult>,
    val status: String
) {
    fun firstLocation(): Location {
        if (results.isEmpty()) return Location(LocationManager.PASSIVE_PROVIDER)
        return results.first().let {
            Location(LocationManager.PASSIVE_PROVIDER).apply {
                latitude = it.geometry?.location?.latitude ?: 0.0
                longitude = it.geometry?.location?.longitude ?: 0.0
            }
        }
    }
}

data class GeoCodeResult(val geometry: GeoCodeResultGeometry?)
data class GeoCodeResultGeometry(val location: GeoCodeResultLocation?)
data class GeoCodeResultLocation(
    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lng")
    val longitude: Double
)


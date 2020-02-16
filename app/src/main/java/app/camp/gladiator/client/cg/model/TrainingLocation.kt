package app.camp.gladiator.client.cg.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class TrainingLocation(
    @SerializedName("placeName")
    val name: String,
    @SerializedName("placeLatitude")
    val latitude: Double,
    @SerializedName("placeLongitude")
    val longitude: Double
) {
    val containValidCoordinates: Boolean
        get() {
            return latitude.isFinite() && longitude.isFinite()
        }

    fun asLatLng(): LatLng = LatLng(latitude, longitude)
}

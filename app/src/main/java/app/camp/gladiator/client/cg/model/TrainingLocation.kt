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
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}

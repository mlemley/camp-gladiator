package app.camp.gladiator.client.cg.model

import android.location.Location


data class TrainingLocationsLookupRequest(
    val latitude: Double,
    val longitude: Double,
    val radius: Int = 25
) {
    fun toMap(): Map<String, String>  = mapOf(
        Pair("lat", "$latitude"),
        Pair("lon", "$longitude"),
        Pair("radius", "$radius")
    )

    companion object {
        fun from(location: Location, radius: Int = 25): TrainingLocationsLookupRequest =
            TrainingLocationsLookupRequest(
                latitude = location.latitude,
                longitude = location.longitude,
                radius = radius
            )
    }
}
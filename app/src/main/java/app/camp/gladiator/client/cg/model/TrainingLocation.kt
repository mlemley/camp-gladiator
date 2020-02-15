package app.camp.gladiator.client.cg.model

import com.google.gson.annotations.SerializedName

data class TrainingLocation(
    @SerializedName("placeName")
    val name:String,
    @SerializedName("placeLatitude")
    val latitude:Double,
    @SerializedName("placeLongitude")
    val longitude:Double
)

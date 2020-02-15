package app.camp.gladiator.client.cg.model


data class TrainingLocationsLookupResponse(
    val success: Boolean,
    val message: String,
    val data: List<TrainingLocation>
)

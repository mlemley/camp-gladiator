package app.camp.gladiator.client.cg

import app.camp.gladiator.client.BaseClient
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupRequest

class CampGladiatorApiClient(
    private val api: CampGladiatorApi
) : BaseClient() {
    suspend fun trainingLocations(trainingLocationsLookupRequest: TrainingLocationsLookupRequest): List<TrainingLocation> =
        safeApiCall(
            call = { api.trainingLocationsNear(trainingLocationsLookupRequest.toMap()).await() },
            errorMessage = "Error fetching locations for $trainingLocationsLookupRequest.toMap()"
        )?.data ?: emptyList()

}

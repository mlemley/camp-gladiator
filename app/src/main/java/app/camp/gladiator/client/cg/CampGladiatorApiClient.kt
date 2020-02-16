package app.camp.gladiator.client.cg

import app.camp.gladiator.client.BaseClient
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupRequest
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupResponse

class CampGladiatorApiClient(
    private val api: CampGladiatorApi
) : BaseClient() {
    suspend fun trainingLocations(trainingLocationsLookupRequest: TrainingLocationsLookupRequest): List<TrainingLocation> {
        val fetchTrainingLocations = fetchTrainingLocations(trainingLocationsLookupRequest)
        return fetchTrainingLocations?.data ?: emptyList()
    }

    private suspend fun fetchTrainingLocations(trainingLocationsLookupRequest: TrainingLocationsLookupRequest): TrainingLocationsLookupResponse? {
        return safeApiCall(
            call = { api.trainingLocationsNear(trainingLocationsLookupRequest.toMap()).await() },
            errorMessage = "Error fetching locations for $trainingLocationsLookupRequest.toMap()"
        )
    }
}

package app.camp.gladiator.repository

import android.location.Location
import app.camp.gladiator.client.cg.CampGladiatorApiClient
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupRequest

class TrainingLocationsRepository constructor(
    private val campGladiatorApiClient: CampGladiatorApiClient
) {

    suspend fun trainingFacilitiesNear(location: Location): List<TrainingLocation> {
        return campGladiatorApiClient.trainingLocations(TrainingLocationsLookupRequest.from(location))
    }
}

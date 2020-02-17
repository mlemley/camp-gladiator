package app.camp.gladiator.client.google

import app.camp.gladiator.client.BaseClient
import app.camp.gladiator.client.google.model.GeoCodeResponse


class GoogleApiClient constructor(val api: GoogleApi, val apiKey: String) : BaseClient() {

    suspend fun geoCode(criteria: String): GeoCodeResponse? {
        return safeApiCall(
            call = {
                api.geoCodeAsync(apiKey = apiKey, address = criteria).await()
            },
            errorMessage = "Error fetching locations for $criteria"
        )
    }

}
package app.camp.gladiator.client.cg

import app.camp.gladiator.client.cg.model.TrainingLocationsLookupResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface CampGladiatorApi {
    @GET("/api/v2/places/searchbydistance")
    fun trainingLocationsNear(@QueryMap args: Map<String, String>): Deferred<Response<TrainingLocationsLookupResponse>>
}
package app.camp.gladiator.client.google

import app.camp.gladiator.client.google.model.GeoCodeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleApi {

    @GET("/maps/api/geocode/json")
    fun geoCodeAsync(@Query("key") apiKey: String, @Query("address") address: String): Deferred<Response<GeoCodeResponse>>
}
package app.camp.gladiator.client.google

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleApiFactory {
    private const val apiBaseUrl = "https://www.googleapis.com/"

    private val client = OkHttpClient().newBuilder()
        // add interceptors here
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    fun googleApiClient(apiKey: String, baseUrl: String = apiBaseUrl): GoogleApiClient = GoogleApiClient(
        retrofit(
            baseUrl
        ).create(GoogleApi::class.java),
        apiKey
    )

}

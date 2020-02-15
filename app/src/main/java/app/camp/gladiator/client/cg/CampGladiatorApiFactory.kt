package app.camp.gladiator.client.cg

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CampGladiatorApiFactory {
    private const val apiBaseUrl = "https://api.pro.coinbase.com"

    private val client = OkHttpClient().newBuilder()
        // add interceptors here
        .build()

    private fun retrofit(baseUrl:String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    fun campGladiatorApiClient(baseUrl: String = apiBaseUrl): CampGladiatorApiClient =
        CampGladiatorApiClient(
            retrofit(
                baseUrl
            ).create(CampGladiatorApi::class.java)
        )
}
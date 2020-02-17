package app.camp.gladiator.client.google

import android.location.Location
import android.location.LocationManager
import app.camp.gladiator.app.Helpers.enqueueSuccessfulResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

@FlowPreview
@ExperimentalCoroutinesApi
class GoogleApiClientTest {

    companion object {
        private const val apiKey: String = "--api-key--"
    }

    private val mockWebServer = MockWebServer()

    private fun createClient(): GoogleApiClient {
        return GoogleApiFactory.googleApiClient(
            apiKey = apiKey,
            baseUrl = mockWebServer.url("/").toString()
        )
    }

    @Before
    fun setUp() {
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetches_location_information_for_given_criteria() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.geoCodeResult)

        val criteria = "Things and stuff"
        val result = createClient().geoCode(criteria)
        val recordedRequest = mockWebServer.takeRequest(100, TimeUnit.NANOSECONDS)!!

        assertThat(recordedRequest.path).isEqualTo(
            "/maps/api/geocode/json?apiKey=--api-key--&address=Things%20and%20stuff"
        )
        val expectedLocation: Location = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 40.7142484
            longitude = -73.9614103
        }

        assertThat(result).isNotNull()
        val actual = result!!.firstLocation()
        assertThat(actual.latitude).isEqualTo(expectedLocation.latitude)
        assertThat(actual.longitude).isEqualTo(expectedLocation.longitude)
    }
}
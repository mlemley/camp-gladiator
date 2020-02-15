package app.camp.gladiator.client.cg

import app.camp.gladiator.app.Helpers.enqueueSuccessfulResponse
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupRequest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@FlowPreview
@ExperimentalCoroutinesApi
class CampGladiatorApiClientTest {
    private val mockWebServer = MockWebServer()

    private fun createClient(): CampGladiatorApiClient {
        return CampGladiatorApiFactory.campGladiatorApiClient(baseUrl = mockWebServer.url("/").toString())
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
    fun fetches_training_location_for_given_criteria() = runBlocking {
        enqueueSuccessfulResponse(mockWebServer, 200, TestData.trainingSearchResults)
        val request = TrainingLocationsLookupRequest(
            latitude = 30.406991,
            longitude = -97.720310
        )

        val trainingLocations = createClient().trainingLocations(request)
        val recordedRequest = mockWebServer.takeRequest(100, TimeUnit.NANOSECONDS)!!

        val expectedLocations: List<TrainingLocation> = listOf(
            TrainingLocation(
                name = "North Austin: Domain North Side - Whole Foods",
                latitude = 30.406991,
                longitude = -97.720310
            )
        )
        assertThat(recordedRequest.path).isEqualTo("/api/v2/places/searchbydistance?lat=${request.latitude}&lon=${request.longitude}&radius=${request.radius}")
        assertThat(trainingLocations).isEqualTo(expectedLocations)
    }
}
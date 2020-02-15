package app.camp.gladiator.repository

import android.location.Location
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.client.cg.CampGladiatorApiClient
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.client.cg.model.TrainingLocationsLookupRequest
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrainingLocationsRepositoryTest {
    private fun createRepository(
        campGladiatorApiClient: CampGladiatorApiClient = mockk(relaxUnitFun = true)
    ): TrainingLocationsRepository =
        TrainingLocationsRepository(campGladiatorApiClient)

    @Test
    fun provides_access_to_training_locations() {
        val trainingLocations = listOf(mockk<TrainingLocation>())
        val location = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 30.406991
            longitude = -97.720310
        }

        val client = mockk<CampGladiatorApiClient> {

            every {
                runBlocking {
                    trainingLocations(
                        TrainingLocationsLookupRequest(
                            latitude = 30.406991,
                            longitude = -97.720310

                        )
                    )
                }
            } returns trainingLocations
        }
        val repository = createRepository(client)

        runBlocking {
            assertThat(repository.trainingFacilitiesNear(location)).isEqualTo(trainingLocations)
        }
    }


}
package app.camp.gladiator.viewmodel.usecase

import android.location.Location
import android.location.LocationManager
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.repository.LocationRepository
import app.camp.gladiator.repository.TrainingLocationsRepository
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.usecase.CampGladiatorLocationsUseCase.Actions
import app.camp.gladiator.viewmodel.usecase.CampGladiatorLocationsUseCase.Results
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class CampGladiatorLocationsUseCaseTest {

    private fun createUseCase(
        locationRepository: LocationRepository = mockk(relaxUnitFun = true),
        trainingLocationsRepository: TrainingLocationsRepository = mockk(relaxUnitFun = true)

    ): CampGladiatorLocationsUseCase = CampGladiatorLocationsUseCase(
        locationRepository,
        trainingLocationsRepository
    )

    @Test
    fun can_process_its_actions() {
        val useCase = createUseCase()

        assertThat(useCase.canProcess(Actions.GatherLocationsNearMe)).isTrue()
        assertThat(
            useCase.canProcess(
                Actions.GatherLocationsNear(
                    Location(LocationManager.PASSIVE_PROVIDER)
                )
            )
        ).isTrue()
        assertThat(useCase.canProcess(object : Action {})).isFalse()
    }

    @Test
    fun fetches_locations_near_user() {
        val location = mockk<Location>()
        val locationRepository = mockk<LocationRepository> {
            every { runBlocking { lastKnownLocation() } } returns location
        }
        val trainingLocations = listOf<TrainingLocation>(mockk())
        val trainingLocationsRepository = mockk<TrainingLocationsRepository> {
            every { runBlocking { trainingFacilitiesNear(location) } } returns trainingLocations
        }

        val useCase = createUseCase(locationRepository, trainingLocationsRepository)

        runBlocking {
            val results = useCase.handleAction(Actions.GatherLocationsNearMe).toList()
            assertThat(results[0]).isEqualTo(Results.LocationsLoading)
            assertThat(results[1]).isEqualTo(Results.LocationsGathered(trainingLocations))
        }
    }

    @Test
    fun fetches_locations_near_supplied_location() {
        val location = mockk<Location>()
        val trainingLocations = listOf<TrainingLocation>(mockk())
        val trainingLocationsRepository = mockk<TrainingLocationsRepository> {
            every { runBlocking { trainingFacilitiesNear(location) } } returns trainingLocations
        }

        val useCase = createUseCase(trainingLocationsRepository = trainingLocationsRepository)

        runBlocking {
            val results = useCase.handleAction(Actions.GatherLocationsNear(location)).toList()
            assertThat(results[0]).isEqualTo(Results.LocationsLoading)
            assertThat(results[1]).isEqualTo(Results.LocationsGathered(trainingLocations))
        }
    }
}
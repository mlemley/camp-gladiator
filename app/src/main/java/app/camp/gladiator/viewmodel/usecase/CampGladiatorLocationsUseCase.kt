package app.camp.gladiator.viewmodel.usecase

import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.repository.LocationRepository
import app.camp.gladiator.repository.TrainingLocationsRepository
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.Result
import app.camp.gladiator.viewmodel.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn

@FlowPreview
@ExperimentalCoroutinesApi
class CampGladiatorLocationsUseCase(
    private val locationRepository: LocationRepository,
    private val trainingLocationsRepository: TrainingLocationsRepository
) : UseCase {

    sealed class Actions : Action {
        object GatherLocationsNearMe : Actions()
        data class GatherLocationsNear(val location: Location) : Actions()
    }

    sealed class Results : Result {
        object LocationsLoading : Results()
        data class LocationsGathered(
            val locations: List<TrainingLocation>,
            val usersLocation: Location? = null
        ) : Results()
    }

    override fun canProcess(action: Action): Boolean = action is Actions

    override fun handleAction(action: Action): Flow<Result> = when (action) {
        is Actions.GatherLocationsNearMe -> handleGatherNearMe()
        is Actions.GatherLocationsNear -> handleGatherNear(action.location)
        else -> emptyFlow()
    }

    private fun handleGatherNear(location: Location): Flow<Result> = channelFlow<Result> {
        send(Results.LocationsLoading)
        send(
            Results.LocationsGathered(
                trainingLocationsRepository.trainingFacilitiesNear(
                    location
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    private fun handleGatherNearMe(): Flow<Result> = channelFlow<Result> {
        send(Results.LocationsLoading)
        val location = locationRepository.lastKnownLocation()
        send(
            Results.LocationsGathered(
                trainingLocationsRepository.trainingFacilitiesNear(
                    location
                ),
                location
            )
        )
    }.flowOn(Dispatchers.IO)

}

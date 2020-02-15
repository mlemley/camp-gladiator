package app.camp.gladiator.ui.locations

import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.model.AppPermission
import app.camp.gladiator.viewmodel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class LocationsViewModel :
    BaseViewModel<LocationsViewModel.Events, LocationsViewModel.LocationsState>() {
    sealed class Events : Event {
        object GatherLocationsNearMe : Events()
    }

    data class LocationsState(
        val locations: List<TrainingLocation> = emptyList(),
        val locationPermission: AppPermission? = null,
        val userLocation: Location? = null
    ) : State

    override val useCases: List<UseCase> = emptyList()

    override fun makeInitState(): LocationsState = LocationsState()

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                Events.GatherLocationsNearMe -> TODO()
            }.exhaustive
        }
    }

    override fun LocationsState.plus(result: Result): LocationsState {
        return when (result) {
            else -> this
        }
    }

}

package app.camp.gladiator.ui.locations

import android.app.Activity
import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.util.Permission
import app.camp.gladiator.util.PermissionUtil
import app.camp.gladiator.viewmodel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class LocationsViewModel(
    val permissionUtil: PermissionUtil,
    val permissionRationale: String
) :
    BaseViewModel<LocationsViewModel.Events, LocationsViewModel.LocationsState>() {
    sealed class Events : Event {
        object GatherLocationsNearMe : Events()
        data class PermissionsResponse(val permissions:Map<String, Int>) : Events()
    }

    data class LocationsState(
        val requiredPermission: Permission? = null,
        val permissionRationale: String = "",
        val locations: List<TrainingLocation> = emptyList(),
        val userLocation: Location? = null
    ) : State

    override val useCases: List<UseCase> = emptyList()

    override fun makeInitState(): LocationsState = LocationsState(
        requiredPermission = requiredPermission(),
        permissionRationale = permissionRationale
    )

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.GatherLocationsNearMe -> TODO()
                is Events.PermissionsResponse -> TODO() // Action should Trigger permission check
            }.exhaustive
        }
    }

    override fun LocationsState.plus(result: Result): LocationsState {
        return when (result) {
            else -> this
        }
    }

    private fun requiredPermission(): Permission? {
        return if (permissionUtil.hasPermissionFor(Permission.LocationPermission())) null
        else Permission.LocationPermission()
    }

}

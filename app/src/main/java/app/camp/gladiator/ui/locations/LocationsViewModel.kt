package app.camp.gladiator.ui.locations

import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.repository.PermissionRepository
import app.camp.gladiator.viewmodel.*
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class LocationsViewModel(
    val permissionRepository: PermissionRepository,
    val permissionRationale: String,
    permissionUseCase: PermissionUseCase
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

    override val useCases: List<UseCase> = listOf(permissionUseCase)

    override fun makeInitState(): LocationsState = LocationsState(
        requiredPermission = requiredPermission(),
        permissionRationale = permissionRationale
    )

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.GatherLocationsNearMe -> TODO()
                is Events.PermissionsResponse -> emit(PermissionUseCase.PermissionResponseReceived(it.permissions))
            }.exhaustive
        }
    }

    override fun LocationsState.plus(result: Result): LocationsState {
        return when (result) {
            is PermissionUseCase.Results.LocationPermissionGranted -> copy(requiredPermission = null)
            else -> this
        }
    }

    private fun requiredPermission(): Permission? {
        return if (permissionRepository.hasPermissionFor(Permission.LocationPermission())) null
        else Permission.LocationPermission()
    }

}

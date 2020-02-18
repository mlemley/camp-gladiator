package app.camp.gladiator.ui.locations

import android.location.Location
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.repository.LocationRepository
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.repository.PermissionRepository
import app.camp.gladiator.viewmodel.*
import app.camp.gladiator.viewmodel.usecase.CampGladiatorLocationsUseCase
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

@FlowPreview
@ExperimentalCoroutinesApi
class LocationsViewModel(
    private val permissionRepository: PermissionRepository,
    private val permissionRationale: String,
    private val invalidSearchCriteriaErrorMessage: String,
    permissionUseCase: PermissionUseCase,
    private val locationRepository: LocationRepository,
    campGladiatorLocationsUseCase: CampGladiatorLocationsUseCase
) :
    BaseViewModel<LocationsViewModel.Events, LocationsViewModel.LocationsState>() {
    sealed class Events : Event {
        object GatherLocationsNearMe : Events()
        data class PermissionsResponse(val permissions: Map<String, Int>) : Events()
        data class LocationSearchNear(val searchCriteria: String) : Events()
    }

    data class LocationsState(
        val requiredPermission: Permission? = null,
        val permissionRationale: String = "",
        val locations: List<TrainingLocation> = emptyList(),
        val usersLocation: Location? = null,
        val errorMessage: String? = null,
        val focusOn: Location? = null,
        val isSearching: Boolean = false
    ) : State

    override val useCases: List<UseCase> = listOf(permissionUseCase, campGladiatorLocationsUseCase)

    override fun makeInitState(): LocationsState = runBlocking {
        val usersLocation = locationRepository.lastKnownLocation()
        LocationsState(
            requiredPermission = requiredPermission(),
            permissionRationale = permissionRationale,
            usersLocation = if (usersLocation.latitude == 0.0 || usersLocation.longitude == 0.0) null else usersLocation
        )
    }

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.GatherLocationsNearMe -> emit(CampGladiatorLocationsUseCase.Actions.GatherLocationsNearMe)
                is Events.PermissionsResponse -> emit(
                    PermissionUseCase.PermissionResponseReceived(
                        it.permissions
                    )
                )
                is Events.LocationSearchNear -> emit(
                    CampGladiatorLocationsUseCase.Actions.GatherLocationsNearSearchCriteria(
                        it.searchCriteria
                    )
                )
            }.exhaustive
        }
    }

    override fun LocationsState.plus(result: Result): LocationsState {
        return when (result) {
            is PermissionUseCase.Results.LocationPermissionGranted -> copy(
                requiredPermission = null,
                errorMessage = null,
                isSearching = false
            )
            is CampGladiatorLocationsUseCase.Results.LocationsGathered -> copy(
                locations = result.locations,
                usersLocation = result.usersLocation,
                focusOn = result.focalPoint,
                errorMessage = null,
                isSearching = false
            )
            is CampGladiatorLocationsUseCase.Results.LocationCouldNotBeFound -> copy(
                errorMessage = invalidSearchCriteriaErrorMessage,
                isSearching = false
            )
            is CampGladiatorLocationsUseCase.Results.LocationsLoading -> copy(
                isSearching = true
            )
            else -> this.copy(errorMessage = null, isSearching = false)
        }
    }

    private fun requiredPermission(): Permission? {
        return if (permissionRepository.hasPermissionFor(Permission.LocationPermission())) null
        else Permission.LocationPermission()
    }

}

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
        data class PermissionRequested(val permission: Permission) : LocationsViewModel.Events()
    }

    sealed class PermissionCollectionState {
        object Init : PermissionCollectionState()
        object Requested : PermissionCollectionState()
        object Collected : PermissionCollectionState()
    }

    data class PermissionState(
        val requiredPermission: Permission? = Permission.LocationPermission,
        val permissionCollectionState: PermissionCollectionState = PermissionCollectionState.Init,
        val permissionRationale: String = ""
    )

    sealed class SearchProgressState {
        object Idle : SearchProgressState()
        object Searching : SearchProgressState()
    }

    data class SearchCriteriaState(
        val locations: List<TrainingLocation> = emptyList(),
        val searchProgressState: SearchProgressState = SearchProgressState.Idle,
        val focusOn: Location? = null
    )

    data class LocationsState(
        val permissionState: PermissionState = PermissionState(),
        val searchCriteriaState: SearchCriteriaState = SearchCriteriaState(),
        val usersLocation: Location? = null,
        val errorMessage: String? = null
    ) : State

    override val useCases: List<UseCase> = listOf(permissionUseCase, campGladiatorLocationsUseCase)

    override fun makeInitState(): LocationsState = runBlocking {
        val usersLocation = locationRepository.lastKnownLocation()
        val requiredPermission = requiredPermission()
        LocationsState(
            permissionState = PermissionState(
                requiredPermission = requiredPermission,
                permissionRationale = permissionRationale,
                permissionCollectionState = if (requiredPermission == null) PermissionCollectionState.Collected else PermissionCollectionState.Init
            ),
            usersLocation = if (usersLocation.latitude == 0.0 || usersLocation.longitude == 0.0) null else usersLocation
        )
    }

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.PermissionRequested -> emit(
                    PermissionUseCase.Actions.PermissionRequested(
                        it.permission
                    )
                )

                is Events.GatherLocationsNearMe -> emit(CampGladiatorLocationsUseCase.Actions.GatherLocationsNearMe)
                is Events.PermissionsResponse -> emit(
                    PermissionUseCase.Actions.PermissionResponseReceived(it.permissions)
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
            is PermissionUseCase.Results.PermissionRequestAcknowledged -> copy(
                this.permissionState.copy(
                    permissionCollectionState = PermissionCollectionState.Requested
                )
            )
            is PermissionUseCase.Results.LocationPermissionGranted -> copy(
                this.permissionState.copy(
                    requiredPermission = null,
                    permissionCollectionState = PermissionCollectionState.Collected
                )
            )
            is PermissionUseCase.Results.LocationPermissionDenied -> copy(
                this.permissionState.copy(
                    permissionCollectionState = PermissionCollectionState.Collected
                )
            )
            is CampGladiatorLocationsUseCase.Results.LocationsGathered -> copy(
                searchCriteriaState = this.searchCriteriaState.copy(
                    locations = result.locations,
                    focusOn = result.focalPoint,
                    searchProgressState = SearchProgressState.Idle
                ),
                usersLocation = result.usersLocation,
                errorMessage = null
            )
            is CampGladiatorLocationsUseCase.Results.LocationCouldNotBeFound -> copy(
                searchCriteriaState = this.searchCriteriaState.copy(
                    searchProgressState = SearchProgressState.Idle
                ),
                errorMessage = invalidSearchCriteriaErrorMessage
            )
            is CampGladiatorLocationsUseCase.Results.LocationsLoading -> copy(
                searchCriteriaState = this.searchCriteriaState.copy(
                    searchProgressState = SearchProgressState.Searching
                )
            )
            else -> this.copy(errorMessage = null)
        }
    }

    private fun requiredPermission(): Permission? {
        return if (permissionRepository.hasPermissionFor(Permission.LocationPermission)) null
        else Permission.LocationPermission
    }

}

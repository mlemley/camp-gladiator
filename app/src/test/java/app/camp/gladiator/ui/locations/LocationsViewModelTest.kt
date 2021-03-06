package app.camp.gladiator.ui.locations

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.repository.LocationRepository
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.repository.PermissionRepository
import app.camp.gladiator.ui.locations.LocationsViewModel.*
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.usecase.CampGladiatorLocationsUseCase
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class LocationsViewModelTest {

    companion object {
        const val permissionRationale = "explanation of location usage"
    }

    private fun createViewModel(
        permissionRepository: PermissionRepository = mockk(relaxUnitFun = true) {
            every { hasPermissionFor(Permission.LocationPermission) } returns true
        },
        locationRepository: LocationRepository = mockk(relaxUnitFun = true) {
            every { runBlocking { lastKnownLocation() } } returns mockk(relaxed = true)
        },
        permissionUseCase: PermissionUseCase = mockk(relaxUnitFun = true),
        campGladiatorLocationsUseCase: CampGladiatorLocationsUseCase = mockk(relaxUnitFun = true),
        invalidSearchCriteriaErrorMessage: String = "Invalid Search"

    ): LocationsViewModel = LocationsViewModel(
        permissionRepository,
        permissionRationale,
        invalidSearchCriteriaErrorMessage,
        permissionUseCase,
        locationRepository,
        campGladiatorLocationsUseCase
    )

    @Test
    fun contains_expected_use_cases() {
        val permissionUseCase = mockk<PermissionUseCase>(relaxUnitFun = true)
        val campGladiatorLocationsUseCase =
            mockk<CampGladiatorLocationsUseCase>(relaxUnitFun = true)
        val viewModel = createViewModel(
            permissionUseCase = permissionUseCase,
            campGladiatorLocationsUseCase = campGladiatorLocationsUseCase
        )

        assertThat(viewModel.useCases).isEqualTo(
            listOf(
                permissionUseCase,
                campGladiatorLocationsUseCase
            )
        )
    }

    @Test
    fun make_init_state__require_location_permission() {
        assertThat(
            createViewModel(
                permissionRepository = mockk {
                    every { hasPermissionFor(Permission.LocationPermission) } returns false
                }
            ).makeInitState()
        ).isEqualTo(
            LocationsState(
                permissionState = PermissionState(
                    requiredPermission = Permission.LocationPermission,
                    permissionRationale = permissionRationale
                )
            )
        )
    }

    @Test
    fun make_init_state__require_no_permissions__includes_users_location() {
        val usersLocation: Location = mockk {
            every { latitude } returns 30.406991
            every { longitude } returns -97.720310
        }
        assertThat(
            createViewModel(
                permissionRepository = mockk {
                    every { hasPermissionFor(Permission.LocationPermission) } returns true
                },
                locationRepository = mockk {
                    every { runBlocking { lastKnownLocation() } } returns usersLocation
                }
            ).makeInitState()
        ).isEqualTo(
            LocationsState(
                permissionState = PermissionState(
                    requiredPermission = null,
                    permissionCollectionState = PermissionCollectionState.Collected,
                    permissionRationale = permissionRationale
                ),
                usersLocation = usersLocation
            )
        )
    }

    @Test
    fun event_transform_maps_correctly() {
        val viewModel = createViewModel()
        val permissions = mapOf<String, Int>(
            Pair(
                Permission.LocationPermission.name,
                PackageManager.PERMISSION_GRANTED
            )
        )
        val searchCriteria = "things and stuff"
        val events = flowOf(
            LocationsViewModel.Events.GatherLocationsNearMe,
            LocationsViewModel.Events.LocationSearchNear(searchCriteria),
            LocationsViewModel.Events.PermissionRequested(Permission.LocationPermission),
            LocationsViewModel.Events.PermissionsResponse(permissions)
        )

        val expected = listOf(
            CampGladiatorLocationsUseCase.Actions.GatherLocationsNearMe,
            CampGladiatorLocationsUseCase.Actions.GatherLocationsNearSearchCriteria(searchCriteria),
            PermissionUseCase.Actions.PermissionRequested(Permission.LocationPermission),
            PermissionUseCase.Actions.PermissionResponseReceived(permissions)
        )

        val actual = mutableListOf<Action>()
        runBlocking {
            with(viewModel) {
                events.eventTransform().toList(actual)
            }
        }

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun plus_zeros_permission__when_location_access_granted_or_denied() {
        val permissionRepository: PermissionRepository = mockk {
            every { hasPermissionFor(Permission.LocationPermission) } returns false
        }

        val usersLocation = mockk<Location>()
        val locationRepository: LocationRepository = mockk(relaxUnitFun = true) {
            every { runBlocking { lastKnownLocation() }} returns Location(LocationManager.PASSIVE_PROVIDER) andThen usersLocation
        }
        val viewModel = createViewModel(
            permissionRepository = permissionRepository,
            locationRepository = locationRepository
        )
        val initState = viewModel.makeInitState()
        val results = listOf(
            PermissionUseCase.Results.PermissionRequestAcknowledged(Permission.LocationPermission),
            PermissionUseCase.Results.LocationPermissionDenied,
            PermissionUseCase.Results.LocationPermissionGranted
        )

        val expectedStates = listOf(
            initState.copy(
                permissionState = PermissionState(
                    requiredPermission = Permission.LocationPermission,
                    permissionRationale = permissionRationale,
                    permissionCollectionState = PermissionCollectionState.Requested
                )
            ),
            initState.copy(
                permissionState = PermissionState(
                    requiredPermission = Permission.LocationPermission,
                    permissionRationale = permissionRationale,
                    permissionCollectionState = PermissionCollectionState.Collected
                )
            ),
            initState.copy(
                permissionState = PermissionState(
                    requiredPermission = null,
                    permissionRationale = permissionRationale,
                    permissionCollectionState = PermissionCollectionState.Collected
                ),
                usersLocation = usersLocation
            )
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }


    @Test
    fun plus_merges_user_location() {
        val viewModel = createViewModel()
        val initState = viewModel.makeInitState().copy(errorMessage = "invalid search")
        val usersLocation = mockk<Location>()
        val locations = listOf<TrainingLocation>(mockk())
        val results = listOf(
            CampGladiatorLocationsUseCase.Results.LocationsGathered(
                locations = locations,
                usersLocation = usersLocation,
                focalPoint = usersLocation
            )
        )

        val expectedStates = listOf(
            initState.copy(
                searchCriteriaState = SearchCriteriaState(
                    locations = locations,
                    focusOn = usersLocation
                ),
                usersLocation = usersLocation,
                errorMessage = null
            )
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }

    @Test
    fun plus_merges_locations_fetch_from_training_facility_lookup() {
        val viewModel = createViewModel()
        val initState =
            viewModel.makeInitState().copy(usersLocation = mockk(), errorMessage = "invalid search")
        val locations = listOf<TrainingLocation>(mockk())
        val focalPoint: Location = mockk()
        val results = listOf(
            CampGladiatorLocationsUseCase.Results.LocationsGathered(
                locations = locations,
                focalPoint = focalPoint
            )
        )

        val expectedStates = listOf(
            initState.copy(
                searchCriteriaState = SearchCriteriaState(
                    locations = locations,
                    focusOn = focalPoint
                ),
                usersLocation = null,
                errorMessage = null
            )
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }

    @Test
    fun plus_adds_error_message_for_invalid_search_criteria() {
        val errorMessage = "Invalid Search"
        val viewModel = createViewModel(invalidSearchCriteriaErrorMessage = errorMessage)
        val initState = viewModel.makeInitState()
        val results = listOf(
            CampGladiatorLocationsUseCase.Results.LocationCouldNotBeFound
        )

        val expectedStates = listOf(
            initState.copy(errorMessage = errorMessage)
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }

    @Test
    fun plus_merges_adds_searching_state() {
        val errorMessage = "Invalid Search"
        val viewModel = createViewModel(invalidSearchCriteriaErrorMessage = errorMessage)
        val initState = viewModel.makeInitState()
        val results = listOf(
            CampGladiatorLocationsUseCase.Results.LocationsLoading
        )

        val expectedStates = listOf(
            initState.copy(
                searchCriteriaState = initState.searchCriteriaState.copy(
                    searchProgressState = SearchProgressState.Searching
                )
            )
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }

    @Test
    fun plus_merges_removes_searching_state() {
        val errorMessage = "Invalid Search"
        val viewModel = createViewModel(invalidSearchCriteriaErrorMessage = errorMessage)
        val initState = viewModel.makeInitState().copy(
            searchCriteriaState = SearchCriteriaState(searchProgressState = SearchProgressState.Searching)
        )
        val results = listOf(
            CampGladiatorLocationsUseCase.Results.LocationCouldNotBeFound,
            CampGladiatorLocationsUseCase.Results.LocationsGathered(emptyList())
        )

        val expectedStates = listOf(
            initState.copy(
                searchCriteriaState = initState.searchCriteriaState.copy(
                    searchProgressState = SearchProgressState.Idle
                ), errorMessage = errorMessage
            ),
            initState.copy(
                searchCriteriaState = initState.searchCriteriaState.copy(
                    searchProgressState = SearchProgressState.Idle,
                    locations = emptyList()
                )
            )
        )

        val actualStates = mutableListOf < LocationsState >()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }
}
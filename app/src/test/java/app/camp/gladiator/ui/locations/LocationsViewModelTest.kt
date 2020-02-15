package app.camp.gladiator.ui.locations

import android.content.pm.PackageManager
import app.camp.gladiator.ui.locations.LocationsViewModel.LocationsState
import app.camp.gladiator.util.Permission
import app.camp.gladiator.util.PermissionUtil
import app.camp.gladiator.viewmodel.Action
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
        permissionUtil: PermissionUtil = mockk(relaxUnitFun = true) {
            every { hasPermissionFor(Permission.LocationPermission()) } returns true
        },
        permissionUseCase: PermissionUseCase = mockk(relaxUnitFun = true)
    ): LocationsViewModel = LocationsViewModel(
        permissionUtil,
        permissionRationale,
        permissionUseCase
    )

    @Test
    fun make_init_state__require_location_permission() {
        assertThat(
            createViewModel(
                permissionUtil = mockk {
                    every { hasPermissionFor(Permission.LocationPermission()) } returns false
                }
            ).makeInitState()
        ).isEqualTo(
            LocationsState(
                requiredPermission = Permission.LocationPermission(),
                permissionRationale = permissionRationale
            )
        )
    }

    @Test
    fun make_init_state__require_no_permissions() {
        assertThat(
            createViewModel(
                permissionUtil = mockk {
                    every { hasPermissionFor(Permission.LocationPermission()) } returns true
                }
            ).makeInitState()
        ).isEqualTo(
            LocationsState(
                permissionRationale = permissionRationale
            )
        )
    }

    @Test
    fun contains_expected_use_cases() {
        val permissionUseCase = mockk<PermissionUseCase>(relaxUnitFun = true)
        val viewModel = createViewModel(permissionUseCase = permissionUseCase)

        assertThat(viewModel.useCases).isEqualTo(listOf(permissionUseCase))
    }

    @Test
    fun event_transform_maps_correctly() {
        val viewModel = createViewModel()
        val permissions = mapOf<String, Int>(
            Pair(
                Permission.LocationPermission().name,
                PackageManager.PERMISSION_GRANTED
            )
        )
        val events = flowOf(
            //LocationsViewModel.Events.GatherLocationsNearMe,
            LocationsViewModel.Events.PermissionsResponse(permissions)
        )

        val expected = listOf(
            PermissionUseCase.PermissionResponseReceived(permissions)
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
    fun plus_zeros_permission__when_location_access_granted() {
        val permissionUtil: PermissionUtil = mockk {
            every { hasPermissionFor(Permission.LocationPermission()) } returns false
        }

        val viewModel = createViewModel(permissionUtil = permissionUtil)
        val initState = viewModel.makeInitState()
        val results = listOf(
            PermissionUseCase.Results.LocationPermissionDenied,
            PermissionUseCase.Results.LocationPermissionGranted
        )

        val expectedStates = listOf(
            initState,
            initState.copy(requiredPermission = null)
        )

        val actualStates = mutableListOf<LocationsState>()
        with(viewModel) {
            results.forEach {
                actualStates.add(initState + it)
            }
        }

        assertThat(actualStates).isEqualTo(expectedStates)
    }
}
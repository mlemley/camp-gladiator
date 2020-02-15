package app.camp.gladiator.ui.locations

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.denyPermissions
import app.camp.gladiator.app.Helpers.loadModules
import app.camp.gladiator.repository.Permission
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAlertDialog

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocationsFragmentTest {

    private fun createScenario(
        liveDataState: LiveData<LocationsViewModel.LocationsState> = mockk(relaxUnitFun = true),
        locationsViewModel: LocationsViewModel = mockk(relaxed = true) {
            every { state } returns liveDataState
        }

    ): FragmentScenario<LocationsFragment> {
        val module = module {
            viewModel { locationsViewModel }
        }
        loadModules(module)
        return FragmentScenario.launchInContainer(LocationsFragment::class.java)
    }

    @Test
    fun observes_state_change__when_created() {
        val state: LiveData<LocationsViewModel.LocationsState> = mockk(relaxUnitFun = true)

        createScenario(state).onFragment { fragment ->
            verify {
                state.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
            }
        }
    }

    @Test
    fun dispatches_init_event_when_loaded() {
        val viewModel: LocationsViewModel = mockk(relaxUnitFun = true) {
            every { state } returns mockk(relaxUnitFun = true)
        }
        createScenario(locationsViewModel = viewModel).onFragment { fragment ->
            fragment.stateObserver.onChanged(
                LocationsViewModel.LocationsState()
            )

            verify {
                viewModel.dispatchEvent(LocationsViewModel.Events.GatherLocationsNearMe)
            }
        }
    }

    @Ignore
    @Test
    fun permission_request__requests_when_and_do_not_require_rationale() {
        // TODO Test belongs in espresso
    }

    @Ignore
    @Test
    fun permission_request__show_required_rationale__when_permission_already_denied() {
        // TODO Test belongs in espresso
        denyPermissions(Permission.LocationPermission().name)

        val permissionRationale = "This is the reason for the permission"
        createScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(
                LocationsViewModel.LocationsState(
                    requiredPermission = Permission.LocationPermission(),
                    permissionRationale = permissionRationale

                )
            )

            val dialog = ShadowAlertDialog.getLatestAlertDialog()
            assertThat(dialog).isNotNull()
            assertThat(shadowOf(dialog).message).isEqualTo(permissionRationale)

        }
    }
}
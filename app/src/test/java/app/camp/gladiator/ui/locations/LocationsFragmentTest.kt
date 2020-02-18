package app.camp.gladiator.ui.locations

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.loadModules
import app.camp.gladiator.app.Helpers.themeId
import app.camp.gladiator.ui.locations.LocationsViewModel.*
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.shadows.ShadowAlertDialog

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocationsFragmentTest {

    private fun createScenario(
        liveDataState: LiveData<LocationsState> = mockk(relaxUnitFun = true),
        locationsViewModel: LocationsViewModel = mockk(relaxed = true) {
            every { state } returns liveDataState
        },

        mapController: MapController = mockk(relaxUnitFun = true)

    ): FragmentScenario<LocationsFragment> {
        val module = module {
            viewModel { locationsViewModel }
            factory { mapController }
        }
        loadModules(module)
        return FragmentScenario.launchInContainer(
            LocationsFragment::class.java,
            null,
            themeId,
            null
        )
    }

    @Test
    fun observes_state_change__when_created() {
        val state: LiveData<LocationsState> = mockk(relaxUnitFun = true)

        createScenario(state).onFragment { fragment ->
            verify {
                state.observe(fragment.viewLifecycleOwner, fragment.stateObserver)
            }
        }
    }

    @Test
    fun gather_locations_near_user_when_permission_collected__and_user_location_present() {
        val viewModel: LocationsViewModel = mockk(relaxUnitFun = true) {
            every { state } returns mockk(relaxUnitFun = true)
        }
        createScenario(locationsViewModel = viewModel).onFragment { fragment ->
            fragment.stateObserver.onChanged(
                LocationsState(
                    permissionState = PermissionState(
                        requiredPermission = null,
                        permissionCollectionState = PermissionCollectionState.Collected
                    ),
                    usersLocation = mockk {
                        every { latitude } returns 30.406991
                        every { longitude } returns -97.720310
                    },
                    searchCriteriaState = SearchCriteriaState(
                        locations = emptyList(),
                        searchProgressState = SearchProgressState.Idle,
                        focusOn = null
                    )
                )
            )

            verify {
                viewModel.dispatchEvent(LocationsViewModel.Events.GatherLocationsNearMe)
            }
        }
    }

    @Test
    fun sets_search_query_observer_on_search_view() {
        val viewModel: LocationsViewModel = mockk(relaxUnitFun = true) {
            every { state } returns mockk(relaxUnitFun = true)
        }
        createScenario(locationsViewModel = viewModel).onFragment { fragment ->
            val searchCriteria = "Things and Stuff"
            fragment.searchView!!.setQuery(searchCriteria, true)

            verify {
                viewModel.dispatchEvent(LocationsViewModel.Events.LocationSearchNear(searchCriteria))
            }
        }
    }

    @Test
    fun shows_error_message_when_instructed() {
        val errorMessage = "Some error to show"
        createScenario().onFragment { fragment ->
            fragment.stateObserver.onChanged(LocationsState(errorMessage = errorMessage))

            val error = ShadowAlertDialog.getLatestDialog()
            assertThat(error).isNotNull()
            assertThat((error as AlertDialog).findViewById<TextView>(android.R.id.message)!!.text).isEqualTo(
                errorMessage
            )
        }
    }

    @Test
    fun renders_searching_loader_when_searching() {
        createScenario().onFragment { fragment ->
            val initialState = LocationsState()

            fragment.stateObserver.onChanged(initialState)
            assertThat(fragment.searchProgressIndicator?.visibility).isEqualTo(View.GONE)

            fragment.stateObserver.onChanged(
                initialState.copy(
                    searchCriteriaState = initialState.searchCriteriaState.copy(
                        searchProgressState = SearchProgressState.Searching
                    )
                )
            )
            assertThat(fragment.searchProgressIndicator?.visibility).isEqualTo(View.VISIBLE)


            fragment.stateObserver.onChanged(
                initialState.copy(
                    searchCriteriaState = initialState.searchCriteriaState.copy(
                        searchProgressState = SearchProgressState.Idle
                    )
                )
            )
            assertThat(fragment.searchProgressIndicator?.visibility).isEqualTo(View.GONE)
        }
    }
}
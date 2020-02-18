package app.camp.gladiator.ui.locations

import android.location.Location
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.loadModules
import app.camp.gladiator.app.Helpers.themeId
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Ignore
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
        liveDataState: LiveData<LocationsViewModel.LocationsState> = mockk(relaxUnitFun = true),
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
            fragment.stateObserver.onChanged(LocationsViewModel.LocationsState(errorMessage = errorMessage))

            val error = ShadowAlertDialog.getLatestDialog()
            assertThat(error).isNotNull()
            assertThat((error as AlertDialog).findViewById<TextView>(android.R.id.message)!!.text).isEqualTo(
                errorMessage
            )
        }
    }

    @Ignore
    @Test
    fun centers_map_on_focal_point() {
        val mapController = mockk<MapController>(relaxUnitFun = true)
        val focalPoint = LatLng(30.406991, -97.720310)
        val location = mockk<Location>(relaxUnitFun = true) {
            every { latitude } returns focalPoint.latitude
            every { longitude } returns focalPoint.longitude
        }
        createScenario(mapController = mapController).onFragment { fragment ->
            fragment.stateObserver.onChanged(LocationsViewModel.LocationsState(focusOn = location))

            excludeRecords {
                mapController.performMapOperation(any(), eq(MapOperations.EnableLocationRendering))
            }

            verify {
                mapController.performMapOperation(any(), MapOperations.CenterOn(focalPoint))
            }
        }
    }
}
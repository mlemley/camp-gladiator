package app.camp.gladiator.ui.locations

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.loadModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.apache.tools.ant.Location
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
}
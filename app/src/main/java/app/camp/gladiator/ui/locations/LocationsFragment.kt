package app.camp.gladiator.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject


@FlowPreview
@ExperimentalCoroutinesApi
class LocationsFragment : Fragment() {

    private val locationsViewModel: LocationsViewModel by inject()

    val stateObserver: Observer<LocationsViewModel.LocationsState> = Observer { state ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        locationsViewModel.state.observe(viewLifecycleOwner, stateObserver)
        locationsViewModel.dispatchEvent(LocationsViewModel.Events.GatherLocationsNearMe)
        return root
    }
}
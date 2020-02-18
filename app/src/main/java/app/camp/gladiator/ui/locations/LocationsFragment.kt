package app.camp.gladiator.ui.locations

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import app.camp.gladiator.extensions.app.toLatLng
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.ui.base.BaseFragment
import app.lemley.crypscape.extensions.app.withView
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject


@FlowPreview
@ExperimentalCoroutinesApi
class LocationsFragment : BaseFragment() {

    private val locationsViewModel: LocationsViewModel by inject()
    private val mapController: MapController by inject()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchView: SearchView? get() = withView(R.id.location_search)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val mapFragment: SupportMapFragment?
        get() = childFragmentManager.findFragmentByTag("map") as SupportMapFragment

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val stateObserver: Observer<LocationsViewModel.LocationsState> = Observer { state ->
        when {
            state.requiredPermission != null -> requestPermissionsFor(
                state.requiredPermission,
                state.permissionRationale
            )
            else -> {
                mapFragment?.let { fragment ->
                    mapController.performMapOperation(fragment, MapOperations.EnableLocationRendering)
                    state.usersLocation?.let { location ->
                        mapController.performMapOperation(fragment, MapOperations.CenterOn(location.toLatLng()))
                    }
                }
            }
        }

        when {
            state.locations.isNotEmpty() -> mapFragment?.let {
                mapController.performMapOperation(
                    it,
                    MapOperations.PlotLocations(state.locations)
                )
            }
            else -> locationsViewModel.dispatchEvent(LocationsViewModel.Events.GatherLocationsNearMe)
        }

        state.errorMessage?.let { showMessage(it) }
        state.focusOn?.let { focalPoint ->
            mapFragment?.let {
                mapController.performMapOperation(
                    it,
                    MapOperations.CenterOn(focalPoint.toLatLng())
                )
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchQueryObserver = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = query?.let {
            locationsViewModel.dispatchEvent(LocationsViewModel.Events.LocationSearchNear(query))
            return true
        } ?: false

        override fun onQueryTextChange(newText: String?): Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_locations, container, false)
        locationsViewModel.state.observe(viewLifecycleOwner, stateObserver)
        return view
    }

    override fun onResume() {
        super.onResume()
        searchView?.setOnQueryTextListener(searchQueryObserver)
    }

    override fun onPause() {
        super.onPause()
        searchView?.setOnQueryTextListener(null)
    }

    // TODO: pull up into base class when permissions count are > 1
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionRequestCode -> locationsViewModel.dispatchEvent(
                LocationsViewModel.Events.PermissionsResponse(
                    permissions.zip(grantResults.toTypedArray()).toMap()
                )
            )
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // TODO: pull up into base class when permissions count are > 1
    private fun requestPermissionsFor(permission: Permission, rationale: String) =
        if (shouldShowRequestPermissionRationale(permission.name)) {
            context?.let {
                val dialog = AlertDialog.Builder(it)
                    .setMessage(rationale)
                    .setPositiveButton(getString(R.string.enable)) { dialogInterface: DialogInterface, i: Int ->
                        requestPermissions(arrayOf(permission.name), permissionRequestCode)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(getString(R.string.keep_disabled)) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
                    .show()
            }
        } else {
            requestPermissions(arrayOf(permission.name), permissionRequestCode)
        }


    companion object {
        const val permissionRequestCode = 1000
    }
}
package app.camp.gladiator.ui.locations

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import app.camp.gladiator.extensions.app.gone
import app.camp.gladiator.extensions.app.show
import app.camp.gladiator.extensions.app.toLatLng
import app.camp.gladiator.extensions.exhaustive
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.ui.base.BaseFragment
import app.camp.gladiator.ui.locations.LocationsViewModel.PermissionCollectionState
import app.camp.gladiator.ui.locations.LocationsViewModel.SearchProgressState
import app.lemley.crypscape.extensions.app.withView
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


@FlowPreview
@ExperimentalCoroutinesApi
class LocationsFragment : BaseFragment() {

    private val locationsViewModel: LocationsViewModel by viewModel()
    private val mapController: MapController by inject()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchView: SearchView?
        get() = withView(R.id.location_search)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchProgressIndicator: ProgressBar?
        get() = withView(R.id.search_progress_indicator)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val mapFragment: SupportMapFragment?
        get() = childFragmentManager.findFragmentByTag("map") as SupportMapFragment

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val stateObserver: Observer<LocationsViewModel.LocationsState> = Observer { state ->
        with(state) {
            when {
                shouldRequestPermission() -> requestPermissionsFor(
                    permissionState.requiredPermission,
                    permissionState.permissionRationale
                )
                searchCriteriaState.searchProgressState == SearchProgressState.Idle
                        && searchCriteriaState.locations.isEmpty()
                        && searchCriteriaState.focusOn == null
                        && usersLocation != null -> locationsViewModel.dispatchEvent(
                    LocationsViewModel.Events.GatherLocationsNearMe
                )
                else -> {
                }
            }

            when {
                usersLocation != null -> performMapOperation(MapOperations.EnableLocationRendering)
                else -> {
                }
            }

            when {
                searchCriteriaState.focusOn != null ->
                    performMapOperation(MapOperations.CenterOn(searchCriteriaState.focusOn.toLatLng()))
                usersLocation != null ->
                    performMapOperation(MapOperations.CenterOn(usersLocation.toLatLng()))
                else -> {
                }
            }

            when (searchCriteriaState.searchProgressState) {
                SearchProgressState.Idle -> {
                    searchProgressIndicator?.gone()
                    performMapOperation(MapOperations.PlotLocations(searchCriteriaState.locations))
                }
                SearchProgressState.Searching -> {
                    searchProgressIndicator?.show()
                }
            }.exhaustive

            if (errorMessage != null ) showMessage(errorMessage)
        }
    }

    private fun performMapOperation(operation: MapOperations) {
        mapFragment?.let { fragment ->
            mapController.performMapOperation(fragment, operation)
        }
    }

    private fun LocationsViewModel.LocationsState.shouldRequestPermission() =
        (permissionState.permissionCollectionState == PermissionCollectionState.Init
                && permissionState.requiredPermission != null)


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val searchQueryObserver = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = query?.let {
            locationsViewModel.dispatchEvent(LocationsViewModel.Events.LocationSearchNear(query))
            focusOffOfSearch()
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
    private fun requestPermissionsFor(permission: Permission?, rationale: String) {
        permission?.let {
            if (shouldShowRequestPermissionRationale(permission.name)) {
                context?.let {
                    AlertDialog.Builder(it)
                        .setMessage(rationale)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.enable)) { dialogInterface: DialogInterface, i: Int ->
                            requestPermissions(arrayOf(permission.name), permissionRequestCode)
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(getString(R.string.keep_disabled)) { dialogInterface: DialogInterface, i: Int ->
                            locationsViewModel.dispatchEvent(LocationsViewModel.Events.PermissionsResponse(
                                mapOf(Pair(permission.name, PackageManager.PERMISSION_DENIED))
                            ))
                            dialogInterface.dismiss()
                        }
                        .show()
                }
            } else {
                requestPermissions(arrayOf(permission.name), permissionRequestCode)
                locationsViewModel.dispatchEvent(
                    LocationsViewModel.Events.PermissionRequested(
                        permission
                    )
                )
            }
        }
    }

    private fun focusOffOfSearch() = searchView?.clearFocus()

    companion object {
        const val permissionRequestCode = 1000
    }
}
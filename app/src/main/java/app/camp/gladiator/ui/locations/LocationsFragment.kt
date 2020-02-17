package app.camp.gladiator.ui.locations

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import app.camp.gladiator.repository.Permission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject


@FlowPreview
@ExperimentalCoroutinesApi
class LocationsFragment : Fragment() {

    private val locationsViewModel: LocationsViewModel by inject()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val mapFragment: SupportMapFragment
        get() = childFragmentManager.findFragmentByTag("map") as SupportMapFragment

    val stateObserver: Observer<LocationsViewModel.LocationsState> = Observer { state ->
        when {
            state.requiredPermission != null -> requestPermissionsFor(
                state.requiredPermission,
                state.permissionRationale
            )
            else -> {
                performMapOperation(MapOperations.EnableLocationRendering)
                state.usersLocation?.let { location ->
                    performMapOperation(MapOperations.CenterOn(location))
                }
            }
        }

        when {
            state.locations.isNotEmpty() -> performMapOperation(MapOperations.PlotLocations(state.locations))
            else -> locationsViewModel.dispatchEvent(LocationsViewModel.Events.GatherLocationsNearMe)
        }
    }

    val onCameraMoveListener = GoogleMap.OnCameraMoveListener {

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

    private fun performMapOperation(mapOperation: MapOperation) {
        mapFragment.getMapAsync { map ->
            mapOperation.operateWith(map)
        }
    }

    companion object {
        const val permissionRequestCode = 1000
    }
}
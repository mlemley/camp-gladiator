package app.camp.gladiator.viewmodel.usecase

import android.content.pm.PackageManager
import app.camp.gladiator.util.Permission
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.Result
import app.camp.gladiator.viewmodel.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class PermissionUseCase : UseCase {
    data class PermissionResponseReceived(val permissions: Map<String, Int>) : Action
    sealed class Results : Result {
        object LocationPermissionGranted : Results()
        object LocationPermissionDenied : Results()
    }


    override fun canProcess(action: Action): Boolean = action is PermissionResponseReceived

    override fun handleAction(action: Action): Flow<Result> {
        return when (action) {
            is PermissionResponseReceived -> handlePermissionResponseReceived(action.permissions)
            else -> emptyFlow()
        }
    }

    private fun handlePermissionResponseReceived(permissions: Map<String, Int>): Flow<Result> =
        channelFlow<Result> {
            permissions.forEach { (permissionName, result) ->
                when {
                    permissionName == Permission.LocationPermission().name &&
                            result == PackageManager.PERMISSION_GRANTED -> send(Results.LocationPermissionGranted)
                    permissionName == Permission.LocationPermission().name &&
                            result == PackageManager.PERMISSION_DENIED -> send(Results.LocationPermissionDenied)
                }
            }
        }.flowOn(Dispatchers.IO)

}

package app.camp.gladiator.viewmodel.usecase

import android.content.pm.PackageManager
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.Result
import app.camp.gladiator.viewmodel.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class PermissionUseCase : UseCase {
    sealed class Actions : Action {
        data class PermissionResponseReceived(val permissions: Map<String, Int>) : Actions()
        data class PermissionRequested(val permission: Permission) : Actions()
    }

    sealed class Results : Result {
        object LocationPermissionGranted : Results()
        object LocationPermissionDenied : Results()
        data class PermissionRequestAcknowledged(val permission: Permission) : Results()
    }


    override fun canProcess(action: Action): Boolean = action is Actions

    override fun handleAction(action: Action): Flow<Result> {
        return when (action) {
            is Actions.PermissionResponseReceived -> handlePermissionResponseReceived(action.permissions)
            is Actions.PermissionRequested -> handlePermissionRequested(action.permission)
            else -> emptyFlow()
        }
    }

    private fun handlePermissionRequested(permission: Permission): Flow<Result> {
        return flowOf(Results.PermissionRequestAcknowledged(permission))
    }

    private fun handlePermissionResponseReceived(permissions: Map<String, Int>): Flow<Result> =
        channelFlow<Result> {
            permissions.forEach { (permissionName, result) ->
                when {
                    permissionName == Permission.LocationPermission.name &&
                            result == PackageManager.PERMISSION_GRANTED -> send(Results.LocationPermissionGranted)
                    permissionName == Permission.LocationPermission.name &&
                            result == PackageManager.PERMISSION_DENIED -> send(Results.LocationPermissionDenied)
                }
            }
        }.flowOn(Dispatchers.IO)

}

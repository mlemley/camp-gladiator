package app.camp.gladiator.viewmodel.usecase

import android.content.pm.PackageManager
import app.camp.gladiator.repository.Permission
import app.camp.gladiator.viewmodel.Action
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase.Actions
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase.Results
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class PermissionUseCaseTest {

    private fun createUseCase(): PermissionUseCase = PermissionUseCase()

    @Test
    fun can_handle_actions() {
        val useCase = createUseCase()
        assertThat(useCase.canProcess(Actions.PermissionResponseReceived(emptyMap()))).isTrue()
        assertThat(useCase.canProcess(object : Action {})).isFalse()
    }

    @Test
    fun handle_action__permission_requested() {
        var actualResult: Results.PermissionRequestAcknowledged? = null

        runBlocking {
            createUseCase().handleAction(
                Actions.PermissionRequested(Permission.LocationPermission)
            ).collect { result ->
                actualResult = result as Results.PermissionRequestAcknowledged
            }
        }

        assertThat(actualResult).isEqualTo(Results.PermissionRequestAcknowledged(Permission.LocationPermission))
    }

    @Test
    fun handle_action__location_access__granted() {
        var actualResult: Results.LocationPermissionGranted? = null

        runBlocking {
            createUseCase().handleAction(
                Actions.PermissionResponseReceived(
                    mapOf(
                        Pair(Permission.LocationPermission.name, PackageManager.PERMISSION_GRANTED)
                    )
                )
            ).collect { result ->
                actualResult = result as Results.LocationPermissionGranted
            }
        }

        assertThat(actualResult).isNotNull()
    }

    @Test
    fun handle_action__location_access__denied() {
        var actualResult: Results.LocationPermissionDenied? = null

        runBlocking {
            createUseCase().handleAction(
                Actions.PermissionResponseReceived(
                    mapOf(
                        Pair(Permission.LocationPermission.name, PackageManager.PERMISSION_DENIED)
                    )
                )
            ).collect { result ->
                actualResult = result as Results.LocationPermissionDenied
            }
        }

        assertThat(actualResult).isNotNull()
    }
}
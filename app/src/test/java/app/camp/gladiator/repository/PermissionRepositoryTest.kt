package app.camp.gladiator.repository

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.grantPermissions
import app.camp.gladiator.app.TestCampGladiatorApplication
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PermissionRepositoryTest {
    private val context: Context =
        ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()

    @Test
    fun checks_for_user_permission__given_user_has_granted_location_permission() {
        grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        assertThat(PermissionRepository(context).hasPermissionFor(Permission.LocationPermission)).isTrue()
    }

    @Test
    fun checks_for_user_permission__given_user_has_not_granted_location_permission() {
        assertThat(PermissionRepository(context).hasPermissionFor(Permission.LocationPermission)).isFalse()
    }


}
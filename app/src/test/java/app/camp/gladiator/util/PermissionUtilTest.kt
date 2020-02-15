package app.camp.gladiator.util

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.TestCampGladiatorApplication
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class PermissionUtilTest {
    private val context: Context =
        ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()

    private fun grantPermissions(vararg permissions: String) {
        shadowOf(
            ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()
        ).grantPermissions(*permissions)
    }

    @Test
    fun checks_for_user_permission__given_user_has_granted_location_permission() {
        grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        assertThat(PermissionUtil(context).hasPermissionFor(Permission.LocationPermission())).isTrue()
    }

    @Test
    fun checks_for_user_permission__given_user_has_not_granted_location_permission() {
        assertThat(PermissionUtil(context).hasPermissionFor(Permission.LocationPermission())).isFalse()
    }


}
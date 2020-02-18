package app.camp.gladiator.repository

import android.location.Location
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.setLastKnownLocation
import app.camp.gladiator.app.TestCampGladiatorApplication
import app.camp.gladiator.extensions.app.locationManager
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocationRepositoryTest {

    private fun createRepository(
        permissionRepository: PermissionRepository = mockk {
            every { hasPermissionFor(Permission.LocationPermission) } returns true
        }
    ): LocationRepository = LocationRepository(
        ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>().locationManager,
        permissionRepository
    )

    @Test
    fun provides_access_to_last_known_location() {
        val location = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 30.377416
            longitude = -97.732010
        }
        setLastKnownLocation(location)

        runBlocking {
            assertThat(createRepository().lastKnownLocation()).isEqualTo(location)
        }
    }


}
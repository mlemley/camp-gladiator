package app.camp.gladiator.app

import android.location.Location
import androidx.test.core.app.ApplicationProvider
import app.camp.gladiator.extensions.app.locationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.robolectric.Shadows.shadowOf


@FlowPreview
@ExperimentalCoroutinesApi
object Helpers {
    fun loadModules(vararg modules: Module) {
        ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>().also {
            loadKoinModules(
                modules.toList()
            )
        }
    }

    fun denyPermissions(vararg permissions: String) {
        shadowOf(
            ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()
        ).denyPermissions(*permissions)
    }

    fun grantPermissions(vararg permissions: String) {
        shadowOf(
            ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()
        ).grantPermissions(*permissions)
    }

    fun setLastKnownLocation(location: Location) {
        shadowOf(
            ApplicationProvider.getApplicationContext<TestCampGladiatorApplication>()
                .locationManager
        ).setLastKnownLocation(location.provider, location)
    }
}


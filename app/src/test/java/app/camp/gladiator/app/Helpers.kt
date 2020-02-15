package app.camp.gladiator.app

import androidx.test.core.app.ApplicationProvider
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.robolectric.Shadows.shadowOf


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
}


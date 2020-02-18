package app.camp.gladiator.app

import android.location.Location
import androidx.test.core.app.ApplicationProvider
import app.camp.gladiator.R
import app.camp.gladiator.extensions.app.locationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.robolectric.Shadows.shadowOf


@FlowPreview
@ExperimentalCoroutinesApi
object Helpers {

    val themeId:Int = R.style.CampGladiator

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

    fun enqueueSuccessfulResponse(mockWebServer: MockWebServer, code: Int, content: String) =
        mockWebServer.enqueue(
            mockSuccessfulResponse(code, content)
        )

    fun mockSuccessfulResponse(code: Int, content: String): MockResponse =
        MockResponse().mockSuccess(code, content)

    fun MockResponse.mockSuccess(code: Int, content: String): MockResponse =
        this.setResponseCode(code).setBody(content)

}


package app.camp.gladiator.ui.locations

import android.location.Location
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.client.cg.model.TrainingLocation
import app.camp.gladiator.extensions.clearPins
import app.camp.gladiator.extensions.moveCameraTo
import app.camp.gladiator.extensions.plot
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapOperationsTest {

    @Test
    fun enable_location_rendering() {
        val map = mockk<GoogleMap>(relaxUnitFun = true)

        MapOperations.EnableLocationRendering.operateWith(map)

        verify {
            map.isMyLocationEnabled = true
        }
    }

    @Ignore // TODO Find way to mock out CameraFactory
    @Test
    fun centers_on_location() {
        val map = mockk<GoogleMap>(relaxUnitFun = true)
        val focalPoint = LatLng(30.406991, -97.720310)

        MapOperations.CenterOn(focalPoint).operateWith(map)

        verify {
            map.moveCameraTo(LatLng(87.0, -79.0))
        }
    }

    @Ignore // TODO Find way to mock out Bipmap factory
    @Test
    fun plots_locations() {
        val map = mockk<GoogleMap>(relaxUnitFun = true)

        val plot1 = LatLng(87.1, -97.1)
        val name1 = "over here"

        val plot2 = LatLng(88.1, -98.1)
        val name2 = "over there"

        val locations = listOf<TrainingLocation>(

            mockk {
                every { toLatLng() } returns plot1
                every { name } returns name1
            },

            mockk {
                every { toLatLng() } returns plot2
                every { name } returns name2
            }
        )

        MapOperations.PlotLocations(locations)

        verifyOrder {
            map.clearPins()
            map.plot(plot1, name1)
            map.plot(plot2, name2)
        }
    }
}
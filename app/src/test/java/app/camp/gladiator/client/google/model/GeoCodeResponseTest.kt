package app.camp.gladiator.client.google.model

import android.location.Location
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.client.google.TestData
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeoCodeResponseTest {

    @Test
    fun example() {
        val geoCodeResponse = Gson().fromJson(TestData.geoCodeResult, GeoCodeResponse::class.java)
        val expectedLocation: Location = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 40.7142484
            longitude = -73.9614103
        }
        val actual = geoCodeResponse.firstLocation()
        assertThat(actual.latitude).isEqualTo(expectedLocation.latitude)
        assertThat(actual.longitude).isEqualTo(expectedLocation.longitude)
    }
}
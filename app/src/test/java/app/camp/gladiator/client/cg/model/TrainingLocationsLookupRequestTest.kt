package app.camp.gladiator.client.cg.model

import android.location.Location
import android.location.LocationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrainingLocationsLookupRequestTest {

    @Test
    fun request_created_from_android_location() {
        val location = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 30.406991
            longitude = -97.720310
        }

        assertThat(TrainingLocationsLookupRequest.from(location, 50)).isEqualTo(
            TrainingLocationsLookupRequest(
                latitude = 30.406991,
                longitude = -97.720310,
                radius = 50
            )
        )
        assertThat(TrainingLocationsLookupRequest.from(location)).isEqualTo(
            TrainingLocationsLookupRequest(
                latitude = 30.406991,
                longitude = -97.720310
            )
        )
    }

}
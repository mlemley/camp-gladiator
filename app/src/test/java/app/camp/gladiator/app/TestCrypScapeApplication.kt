package app.camp.gladiator.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

@FlowPreview
@ExperimentalCoroutinesApi
class TestCampGladiatorApplication : TestLifecycleApplication, CampGladiatorApplication() {


    override fun loadDependencyInjection() {
    }

    override fun beforeTest(method: Method?) {
        startKoin {
            androidLogger()
            androidContext(this@TestCampGladiatorApplication)
        }
    }

    override fun prepareTest(test: Any?) {
    }

    override fun afterTest(method: Method?) {
        stopKoin()
    }

}
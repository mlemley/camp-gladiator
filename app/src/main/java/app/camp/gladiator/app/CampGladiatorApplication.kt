package app.camp.gladiator.app

import android.app.Application
import app.camp.gladiator.ui.app.di.campGladiatorModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
open class CampGladiatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadDependencyInjection()
    }

    protected open fun loadDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(this@CampGladiatorApplication)
            modules(campGladiatorModules)
        }
    }
}
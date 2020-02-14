package app.camp.gladiator.app

import android.app.Application
import app.camp.gladiator.ui.app.di.campGladiatorModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class CampGladiatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadDependencyInjection()
    }

    protected fun loadDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(this@CampGladiatorApplication)
            modules(campGladiatorModules)
        }
    }
}
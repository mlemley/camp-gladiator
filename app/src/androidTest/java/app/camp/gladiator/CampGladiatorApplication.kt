package app.camp.gladiator

import android.app.Application
import app.camp.gladiator.di.campGladiatorModules
import org.koin.core.context.loadKoinModules


class CampGladiatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadDependencyInjection()
    }

    protected fun loadDependencyInjection() {
        loadKoinModules(campGladiatorModules)
    }
}
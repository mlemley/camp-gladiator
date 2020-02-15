package app.camp.gladiator.ui.app.di

import app.camp.gladiator.R
import app.camp.gladiator.client.cg.campGladiatorApiModule
import app.camp.gladiator.extensions.app.locationManager
import app.camp.gladiator.extensions.app.locationServices
import app.camp.gladiator.repository.LocationRepository
import app.camp.gladiator.repository.PermissionRepository
import app.camp.gladiator.repository.TrainingLocationsRepository
import app.camp.gladiator.ui.locations.LocationsViewModel
import app.camp.gladiator.ui.welcome.WelcomeScreenViewModel
import app.camp.gladiator.viewmodel.usecase.CampGladiatorLocationsUseCase
import app.camp.gladiator.viewmodel.usecase.DelayedCallback
import app.camp.gladiator.viewmodel.usecase.PermissionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {
    // Injectable Constants
    single(named("WelcomeScreenDelay")) { 700L }
    single(named("LocationPermissionRationale")) { androidContext().getString(R.string.permission_rationale) }

    // Android System Services
    factory { androidContext().locationServices }
    factory { androidContext().locationManager }

    // Repositories
    single { PermissionRepository(androidContext()) }
    factory { LocationRepository(get(), get()) }
    factory { TrainingLocationsRepository(get()) }

    // UseCases
    factory { DelayedCallback() }
    factory { PermissionUseCase() }
    factory { CampGladiatorLocationsUseCase(get(), get()) }

    viewModel { WelcomeScreenViewModel(get(), get(named("WelcomeScreenDelay"))) }
    viewModel { LocationsViewModel(get(), get(named("LocationPermissionRationale")), get(), get()) }
}

@FlowPreview
@ExperimentalCoroutinesApi
val campGladiatorModules = listOf(
    appModule,
    campGladiatorApiModule
)
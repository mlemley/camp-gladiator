package app.camp.gladiator.ui.app.di

import app.camp.gladiator.R
import app.camp.gladiator.ui.locations.LocationsViewModel
import app.camp.gladiator.ui.welcome.WelcomeScreenViewModel
import app.camp.gladiator.util.PermissionUtil
import app.camp.gladiator.viewmodel.usecase.DelayedCallback
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

    // Util
    single { PermissionUtil(androidContext()) }

    // UseCases
    factory { DelayedCallback() }

    viewModel { WelcomeScreenViewModel(get(), get(named("WelcomeScreenDelay"))) }
    viewModel { LocationsViewModel(get(), get(named("LocationPermissionRationale"))) }
}

@FlowPreview
@ExperimentalCoroutinesApi
val campGladiatorModules = listOf(
    appModule
)
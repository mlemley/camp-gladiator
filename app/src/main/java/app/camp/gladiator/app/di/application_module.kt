package app.camp.gladiator.ui.app.di

import app.camp.gladiator.ui.welcome.WelcomeScreenViewModel
import app.camp.gladiator.viewmodel.usecase.DelayedCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {
    // Injectable Constants
    single(named("WelcomeScreenDelay")) { 700L }

    // UseCases
    factory { DelayedCallback() }

    viewModel {
        WelcomeScreenViewModel(get(), get(named("WelcomeScreenDelay")))
    }

}

@FlowPreview
@ExperimentalCoroutinesApi
val campGladiatorModules = listOf(
    appModule
)
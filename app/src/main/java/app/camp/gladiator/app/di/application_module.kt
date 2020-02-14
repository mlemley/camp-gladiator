package app.camp.gladiator.ui.app.di

import app.camp.gladiator.ui.welcome.WelcomeScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
val appModule = module {

    viewModel {
        WelcomeScreenViewModel(
            listOf()
        )
    }

}

@FlowPreview
@ExperimentalCoroutinesApi
val campGladiatorModules = listOf(
    appModule
)
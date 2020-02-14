package app.camp.gladiator.ui.welcome

import androidx.lifecycle.LiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.loadModules
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WelcomeActivityTest {

    private fun createScenario(
        liveDataState: LiveData<WelcomeScreenViewModel.WelcomeScreenState> = mockk(relaxUnitFun = true)
    ): ActivityScenario<WelcomeActivity> {
        val module = module {
            viewModel {
                mockk<WelcomeScreenViewModel>(relaxUnitFun = true) {
                    every { state } returns liveDataState
                }
            }
        }
        loadModules(module)
        return ActivityScenario.launch(WelcomeActivity::class.java)
    }

    @Test
    fun observes_state_change__when_created() {
        val state: LiveData<WelcomeScreenViewModel.WelcomeScreenState> = mockk(relaxUnitFun = true)

        createScenario(state).onActivity { activity ->
            verify {
                state.observe(activity, activity.stateObserver)
            }
        }.close()
    }


}
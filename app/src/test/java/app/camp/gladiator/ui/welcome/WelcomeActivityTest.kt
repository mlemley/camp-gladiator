package app.camp.gladiator.ui.welcome

import androidx.lifecycle.LiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.camp.gladiator.app.Helpers.loadModules
import app.camp.gladiator.ui.home.HomeActivity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.Shadows.shadowOf

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WelcomeActivityTest {

    private fun createScenario(
        liveDataState: LiveData<WelcomeScreenViewModel.WelcomeScreenState> = mockk(relaxUnitFun = true),
        viewModel: WelcomeScreenViewModel = mockk(relaxed = true) {
            every { state } returns liveDataState
        }
    ): ActivityScenario<WelcomeActivity> {
        val module = module {
            viewModel { viewModel }
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

    @Test
    fun dispatches_init_event_when_loaded() {
        val viewModel:WelcomeScreenViewModel = mockk(relaxUnitFun = true) {
            every { state } returns mockk(relaxUnitFun = true)
        }
        createScenario(viewModel = viewModel).onActivity { activity ->
            verify {
                viewModel.dispatchEvent(WelcomeScreenViewModel.Events.Init)
            }
        }.close()
    }

    @Test
    fun proceeds_forward_when_instructed() {
        createScenario().onActivity { activity ->
            activity.stateObserver.onChanged(
                WelcomeScreenViewModel.WelcomeScreenState(
                    requiredActions = WelcomeScreenViewModel.RequiredActions.ProceedForward
                )
            )

            assertThat(
                shadowOf(activity).peekNextStartedActivity().component?.className
            ).isEqualTo(HomeActivity::class.java.name)
        }.close()
    }

}
package app.camp.gladiator.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import app.camp.gladiator.ui.home.HomeActivity
import app.camp.gladiator.ui.welcome.WelcomeScreenViewModel.RequiredActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class WelcomeActivity : AppCompatActivity() {

    val welcomeViewModel: WelcomeScreenViewModel by viewModel()

    val stateObserver: Observer<WelcomeScreenViewModel.WelcomeScreenState> = Observer { state ->
        when (state.requiredActions) {
            is RequiredActions.ProceedForward -> navigateHome()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        welcomeViewModel.state.observe(this@WelcomeActivity, stateObserver)
        welcomeViewModel.dispatchEvent(WelcomeScreenViewModel.Events.Init)
    }

    private fun navigateHome() {
        Intent(this, HomeActivity::class.java).also {
            startActivity(it)
        }
    }
}

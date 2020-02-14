package app.camp.gladiator.ui.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import app.camp.gladiator.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class WelcomeActivity : AppCompatActivity() {

    val welcomeViewModel: WelcomeScreenViewModel by inject()

    val stateObserver: Observer<WelcomeScreenViewModel.WelcomeScreenState> = Observer { state ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        welcomeViewModel.state.observe(this@WelcomeActivity, stateObserver)
    }
}
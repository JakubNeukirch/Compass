package eu.jakubneukirch.compass.screen.main

import android.os.Bundle
import eu.jakubneukirch.compass.R
import eu.jakubneukirch.compass.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, MainState>() {

    override val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.listenDirectionChanges()
    }

    override fun onStateChanged(state: MainState) {
        setRotation(state.degrees)
        setCordsSwitchState(state is MainState.CordsDirectionState)
    }

    private fun setCordsSwitchState(isCordsState: Boolean) {
        cordsSwitch.isChecked = isCordsState
    }

    private fun setRotation(rotation: Float) {
        compassArrow.rotation = rotation
    }
}

package eu.jakubneukirch.compass.screen.main

import android.os.Bundle
import androidx.lifecycle.Observer
import eu.jakubneukirch.compass.R
import eu.jakubneukirch.compass.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel>() {

    override val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.listenDirectionChanges()
        observeDirection()
    }

    private fun observeDirection() {
        viewModel.direction.observe(this, Observer { degrees ->
            setRotation(-degrees)
        })
    }

    private fun setRotation(rotation: Float) {
        compassArrow.rotation = rotation
    }
}

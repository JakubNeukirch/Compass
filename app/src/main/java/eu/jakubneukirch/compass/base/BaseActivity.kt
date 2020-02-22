package eu.jakubneukirch.compass.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

@SuppressLint("Registered")
abstract class BaseActivity<VM : BaseViewModel<STATE>, STATE> : AppCompatActivity() {
    abstract protected val viewModel: VM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, Observer {
            onStateChanged(it)
        })
    }

    open fun onStateChanged(state: STATE) = Unit
}
package eu.jakubneukirch.compass.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

@SuppressLint("Registered")
abstract class BaseActivity<VM : BaseViewModel<STATE>, STATE> : AppCompatActivity() {
    abstract protected val viewModel: VM
    private val contentView: View by lazy { findViewById<View>(android.R.id.content) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this, Observer {
            onStateChanged(it)
        })
    }

    open fun onStateChanged(state: STATE) = Unit

    protected fun showMessage(text: String) {
        Snackbar.make(contentView, text, Snackbar.LENGTH_LONG).show()
    }
}
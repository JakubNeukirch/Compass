package eu.jakubneukirch.compass.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.viewmodel.ext.android.viewModel

@SuppressLint("Registered")
abstract class BaseActivity<VM: BaseViewModel>: AppCompatActivity() {
    abstract protected val viewModel: VM
}
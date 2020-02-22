package eu.jakubneukirch.compass.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    protected val disposables = CompositeDisposable()
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
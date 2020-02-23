package eu.jakubneukirch.compass.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel<STATE> : ViewModel() {
    protected val disposables = CompositeDisposable()
    protected val mutableState = MutableLiveData<STATE>()
    val state: LiveData<STATE> get() = mutableState

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
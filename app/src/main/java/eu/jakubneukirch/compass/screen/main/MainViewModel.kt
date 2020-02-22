package eu.jakubneukirch.compass.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eu.jakubneukirch.compass.base.BaseViewModel
import eu.jakubneukirch.compass.screen.main.usecase.IGetNorthDirectionUpdates
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class MainViewModel(
    private val _getNorthDirectionUpdates: IGetNorthDirectionUpdates
) : BaseViewModel() {

    private val _direction = MutableLiveData<Float>()
    val direction: LiveData<Float> get() = _direction

    fun listenDirectionChanges() {
        disposables.add(
            _getNorthDirectionUpdates(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { degrees ->
                    _direction.value = degrees
                }
                .doOnError {
                    Timber.e(it)
                }
                .subscribe()
        )
    }
}
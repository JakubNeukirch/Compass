package eu.jakubneukirch.compass.screen.main

import eu.jakubneukirch.compass.base.BaseViewModel
import eu.jakubneukirch.compass.screen.main.usecase.IGetNorthDirectionUpdates
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class MainViewModel(
    private val _getNorthDirectionUpdates: IGetNorthDirectionUpdates
) : BaseViewModel<MainState>() {

    fun listenDirectionChanges() {
        disposables.add(
            _getNorthDirectionUpdates(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { degrees ->
                    mutableState.value = MainState.NorthDirectionState(degrees)
                }
                .doOnError {
                    Timber.e(it)
                }
                .subscribe()
        )
    }

    fun setCords(longitude: Double?, latitude: Double?) {

    }
}
package eu.jakubneukirch.compass.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eu.jakubneukirch.compass.base.BaseViewModel
import eu.jakubneukirch.compass.screen.main.usecase.IGetCoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.IGetNorthDirectionUpdates
import eu.jakubneukirch.compass.service.ProvidersUnavailableException
import eu.jakubneukirch.compass.utils.Event
import eu.jakubneukirch.compass.utils.subscribeBy
import eu.jakubneukirch.compass.utils.toEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val _getNorthDirectionUpdates: IGetNorthDirectionUpdates,
    private val _getCoordinatesDirectionUpdates: IGetCoordinatesDirectionUpdates
) : BaseViewModel<MainState>() {

    companion object {
        private const val INPUT_DEBOUNCE_DELAY_MILLIS = 150L
        const val VALUE_REFRESH_TIME_MILLIS = 100L
    }

    private val _error = MutableLiveData<Event<MainError>>()
    val error: LiveData<Event<MainError>> get() = _error

    private var _northDirectionUpdatesDisposable = SerialDisposable()
    private val _coordinatesDirectionUpdatesDisposable = SerialDisposable()
    private val _inputDisposable = SerialDisposable()

    fun listenNorthDirectionChanges() {
        _northDirectionUpdatesDisposable.set(
            _getNorthDirectionUpdates(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .sample(VALUE_REFRESH_TIME_MILLIS, TimeUnit.MILLISECONDS)
                .subscribeBy(
                    onNext = { degrees ->
                        Timber.i("Degrees")
                        mutableState.postValue(MainState.NorthDirectionState(degrees))
                    },
                    onError = Timber::e
                )
        )
    }

    fun setCoordinates(longitude: Double?, latitude: Double?) {
        _inputDisposable.set(
            Single.timer(INPUT_DEBOUNCE_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .subscribeBy(
                    onSuccess = {
                        calculateWithNewCoordinates(longitude, latitude)
                    },
                    onError = Timber::e
                )
        )
    }

    private fun calculateWithNewCoordinates(longitude: Double?, latitude: Double?) {
        _coordinatesDirectionUpdatesDisposable.set(
            _getCoordinatesDirectionUpdates(
                IGetCoordinatesDirectionUpdates.Params(longitude, latitude)
            )
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .sample(VALUE_REFRESH_TIME_MILLIS, TimeUnit.MILLISECONDS)
                .subscribeBy(
                    onNext = { degrees ->
                        stopListeningNorthDirectionChanges()
                        mutableState.value = MainState.CordsDirectionState(degrees)
                    },
                    onError = {
                        Timber.e("$it")
                        listenNorthDirectionChanges()
                        if (it is ProvidersUnavailableException) {
                            _error.value = MainError.PROVIDERS_UNAVAILABLE.toEvent()
                        }
                    }
                )
        )
    }

    private fun stopListeningNorthDirectionChanges() {
        _northDirectionUpdatesDisposable.dispose()
        _northDirectionUpdatesDisposable = SerialDisposable()
    }

    override fun onCleared() {
        _northDirectionUpdatesDisposable.dispose()
        _coordinatesDirectionUpdatesDisposable.dispose()
        _inputDisposable.dispose()
        super.onCleared()
    }

    enum class MainError {
        PROVIDERS_UNAVAILABLE,
        LOCATION_PERMISSION_NOT_GRANTED,
        UNKNOWN
    }
}
package eu.jakubneukirch.compass.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

class DirectionService(context: Context) {
    private val _sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun listenNorthDirection(): Observable<Float> {
        var listener: SensorEventListener? = null
        val observable = Observable.create<Float> { emitter ->
            listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        Timber.i("event value: ${it.values[0]}, ${it.values[1]}, ${it.values[2]}")
                        emitter.onNext(it.values[0])
                    }
                }
            }
            _sensorManager.registerListener(
                listener,
                _sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI
            )
        }
        return observable.doOnDispose {
            _sensorManager.unregisterListener(listener)
        }
    }
}
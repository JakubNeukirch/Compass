package eu.jakubneukirch.compass.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.rxjava3.core.Observable

class SensorNorthDirectionService(context: Context) : NorthDirectionService {
    companion object {
        private const val UPDATE_INTERVAL_MILLIS = 250L
    }

    private val _sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val _accelerometer: Sensor by lazy { _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val _magnetometer: Sensor by lazy { _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    override fun listenNorthDirection(): Observable<Float> {
        var listener: SensorEventListener? = null
        var isAccelerometerSet = false
        var isMagnetometerSet = false
        val accelerometerValues = FloatArray(3)
        val magnetometerValues = FloatArray(3)
        val rotation = FloatArray(9)
        val orientation = FloatArray(3)
        val observable = Observable.create<Float> { emitter ->
            listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        when (it.sensor) {
                            _accelerometer -> {
                                it.values.copyInto(accelerometerValues, 0, 0, 3)
                                isAccelerometerSet = true
                            }
                            _magnetometer -> {
                                it.values.copyInto(magnetometerValues, 0, 0, 3)
                                isMagnetometerSet = true
                            }
                        }
                        if (isAccelerometerSet && isMagnetometerSet) {
                            SensorManager.getRotationMatrix(
                                rotation,
                                null,
                                accelerometerValues,
                                magnetometerValues
                            )
                            SensorManager.getOrientation(rotation, orientation)

                            emitter.onNext(-Math.toDegrees(orientation[0].toDouble()).toFloat())
                        }
                    }
                }
            }
            _sensorManager.registerListener(
                listener,
                _accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
            _sensorManager.registerListener(
                listener,
                _magnetometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        return observable.doOnDispose {
            _sensorManager.unregisterListener(listener, _accelerometer)
            _sensorManager.unregisterListener(listener, _magnetometer)
        }
    }
}
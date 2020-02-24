package eu.jakubneukirch.compass.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.rxjava3.core.Observable

class SensorNorthDirectionService(context: Context) : NorthDirectionService {
    private val _sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val _accelerometer: Sensor by lazy { _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private val _magnetometer: Sensor by lazy { _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    override fun listenNorthDirection(): Observable<Float> {
        var listener: SensorEventListener? = null
        val observable = Observable.create<Float> { emitter ->

            listener = CompassEventListener(emitter::onNext)

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
            .map {
                if (it < 0) {
                    360 + it
                } else {
                    it
                }
            }
    }

    private inner class CompassEventListener(
        private val onDegreesChanged: (degrees: Float) -> Unit
    ) : SensorEventListener {
        private var _isAccelerometerSet = false
        private var _isMagnetometerSet = false
        private val _accelerometerValues = FloatArray(3)
        private val _magnetometerValues = FloatArray(3)
        private val _rotation = FloatArray(9)
        private val _orientation = FloatArray(3)
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor) {
                    _accelerometer -> {
                        it.values.copyInto(_accelerometerValues, 0, 0, 3)
                        _isAccelerometerSet = true
                    }
                    _magnetometer -> {
                        it.values.copyInto(_magnetometerValues, 0, 0, 3)
                        _isMagnetometerSet = true
                    }
                }
                if (_isAccelerometerSet && _isMagnetometerSet) {
                    SensorManager.getRotationMatrix(
                        _rotation,
                        null,
                        _accelerometerValues,
                        _magnetometerValues
                    )
                    SensorManager.getOrientation(_rotation, _orientation)

                    onDegreesChanged(-Math.toDegrees(_orientation[0].toDouble()).toFloat())
                }
            }
        }
    }
}
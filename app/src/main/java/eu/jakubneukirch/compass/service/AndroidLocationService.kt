package eu.jakubneukirch.compass.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import eu.jakubneukirch.compass.data.model.Coordinates
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

class AndroidLocationService(private val context: Context) : LocationService {
    companion object {
        private const val MIN_UPDATE_DISTANCE_METERS = 1f
        private const val MIN_UPDATE_TIME_MILLIS = 500L
    }

    private val _locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    @Throws(ProvidersUnavailableException::class, LocationPermissionNotGrantedException::class)
    override fun listenToLocation(): Observable<Coordinates> {
        var listener: LocationListener? = null
        val observable = Observable.create<Coordinates> { emitter ->
            if (isPermissionGranted()) {
                listener = createLocationListener {
                    emitter.onNext(Coordinates(it.latitude, it.longitude))
                }
                val provider = getBestPossibleProvider()
                    ?: throw ProvidersUnavailableException()
                Timber.i("Provider $provider")
                _locationManager.getLastKnownLocation(provider)?.let { location ->
                    emitter.onNext(Coordinates(location.latitude, location.longitude))
                }
                _locationManager.requestLocationUpdates(
                    provider,
                    MIN_UPDATE_TIME_MILLIS,
                    MIN_UPDATE_DISTANCE_METERS,
                    listener!!,
                    Looper.getMainLooper()
                )
            } else {
                throw LocationPermissionNotGrantedException()
            }
        }
        observable.doOnDispose {
            listener?.let {
                _locationManager.removeUpdates(it)
            }
        }
        return observable
    }

    @SuppressLint("MissingPermission")
    private fun getBestPossibleProvider(): String? {
        val providers = linkedSetOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        return providers.find { _locationManager.getLastKnownLocation(it) != null }
    }

    private fun createLocationListener(onLocation: (location: Location) -> Unit): LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                location?.let { onLocation(location) }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit

            override fun onProviderEnabled(p0: String?) = Unit

            override fun onProviderDisabled(p0: String?) = Unit

        }
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

class ProvidersUnavailableException : Exception("None of required providers is available.")
class LocationPermissionNotGrantedException :
    Exception("Location Permission not granted, request again.")
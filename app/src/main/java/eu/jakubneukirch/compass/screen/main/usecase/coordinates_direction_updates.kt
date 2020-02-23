package eu.jakubneukirch.compass.screen.main.usecase

import eu.jakubneukirch.compass.base.UseCase
import eu.jakubneukirch.compass.data.model.Coordinates
import eu.jakubneukirch.compass.service.LocationService
import io.reactivex.rxjava3.core.Observable
import kotlin.math.atan2

interface IGetCoordinatesDirectionUpdates :
    UseCase<IGetCoordinatesDirectionUpdates.Params, Observable<Float>> {
    data class Params(
        val longitude: Double?,
        val latitude: Double?
    )
}

class GetCoordinatesDirectionUpdates(
    private val _locationService: LocationService
) : IGetCoordinatesDirectionUpdates {
    override fun run(param: IGetCoordinatesDirectionUpdates.Params): Observable<Float> {
        return if (param.latitude != null && param.longitude != null) {
            _locationService.listenToLocation()
                .map {
                    val radians = calculateAngle(
                        it,
                        Coordinates(param.latitude, param.longitude)
                    )
                    Math.toDegrees(radians).toFloat()
                }
        } else {
            Observable.error(NoSufficientDataException())
        }
    }

    private fun calculateAngle(currentLocation: Coordinates, destination: Coordinates): Double {
        return atan2(
            destination.latitude - currentLocation.latitude,
            destination.longitude - currentLocation.longitude
        )
    }
}

class NoSufficientDataException : Exception("Cords not provided")
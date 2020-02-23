package eu.jakubneukirch.compass.screen.main.usecase

import eu.jakubneukirch.compass.base.UseCase
import eu.jakubneukirch.compass.data.model.Coordinates
import eu.jakubneukirch.compass.service.LocationService
import eu.jakubneukirch.compass.service.NorthDirectionService
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
    private val _locationService: LocationService,
    private val _directionService: NorthDirectionService
) : IGetCoordinatesDirectionUpdates {

    override fun run(param: IGetCoordinatesDirectionUpdates.Params): Observable<Float> {
        return if (param.latitude != null && param.longitude != null) {
            getCompassAngle(param.latitude, param.longitude)
        } else {
            Observable.error(NoSufficientDataException())
        }
    }

    private fun getCompassAngle(latitude: Double, longitude: Double): Observable<Float> {
        return Observable.combineLatest(
            listOf(
                _locationService.listenToLocation(),
                _directionService.listenNorthDirection()
            )
        ) {
            Pair(it[0] as Coordinates, (it[1] as Float))
        }
            .map { (currentLocation, degreesToNorth) ->
                val degrees = calculateAngle(
                    currentLocation,
                    Coordinates(latitude, longitude)
                )
                degreesToNorth + degrees
            }

    }

    private fun calculateAngle(currentLocation: Coordinates, destination: Coordinates): Float {
        val x = destination.latitude - currentLocation.latitude
        val y = destination.longitude - currentLocation.longitude
        val radians = atan2(
            y,
            x
        )
        return Math.toDegrees(radians).toFloat()
    }
}

class NoSufficientDataException : Exception("Cords not provided")
package eu.jakubneukirch.compass.service

import eu.jakubneukirch.compass.data.model.Coordinates
import io.reactivex.rxjava3.core.Observable

interface LocationService {
    fun listenToLocation(): Observable<Coordinates>
}
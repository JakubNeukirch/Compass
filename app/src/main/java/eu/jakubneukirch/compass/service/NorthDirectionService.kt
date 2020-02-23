package eu.jakubneukirch.compass.service

import io.reactivex.rxjava3.core.Observable

interface NorthDirectionService {
    fun listenNorthDirection(): Observable<Float>
}
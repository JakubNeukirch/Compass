package eu.jakubneukirch.compass.screen.main.usecase

import eu.jakubneukirch.compass.base.UseCase
import eu.jakubneukirch.compass.service.NorthDirectionService
import io.reactivex.rxjava3.core.Observable

interface IGetNorthDirectionUpdates : UseCase<Unit, Observable<Float>>

class GetNorthDirectionUpdates(
    private val _directionService: NorthDirectionService
) : IGetNorthDirectionUpdates {
    override fun run(param: Unit): Observable<Float> {
        return _directionService.listenNorthDirection()
    }
}
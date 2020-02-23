package eu.jakubneukirch.compass

import eu.jakubneukirch.compass.data.model.Coordinates
import eu.jakubneukirch.compass.screen.main.usecase.GetCoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.IGetCoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.NoSufficientDataException
import eu.jakubneukirch.compass.service.LocationService
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test

class CoordinatesDirectionTest {

    private lateinit var _coordinatesDirectionUpdates: IGetCoordinatesDirectionUpdates
    private lateinit var _locationService: LocationService

    @Test
    fun `should throw NoSufficientDataException on longitude null`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(null, 0.0))
            .test()
            .assertError { it is NoSufficientDataException }
    }

    @Test
    fun `should throw NoSufficientDataException on latitude null`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(0.0, null))
            .test()
            .assertError { it is NoSufficientDataException }
    }

    @Test
    fun `should throw NoSufficientDataException on both coordinates null`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(null, null))
            .test()
            .assertError { it is NoSufficientDataException }
    }

    @Test
    fun `should have result 0 degrees`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(0.0, 0.0))
            .test()
            .assertValueAt(0) {
                it == 0.0f
            }
    }

    @Test
    fun `should have result 90 degrees`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(0.0, 10.0))
            .test()
            .assertValueAt(0) {
                println(it)
                it == 90.0f
            }
    }

    @Test
    fun `should have result 180 degrees`() {
        _coordinatesDirectionUpdates(IGetCoordinatesDirectionUpdates.Params(-10.0, 0.0))
            .test()
            .assertValueAt(0) {
                println(it)
                it == 180.0f
            }
    }

    @Before
    fun setup() {
        _locationService = mockk {
            every { listenToLocation() } returns Observable.just(Coordinates(0.0, 0.0))
        }
        _coordinatesDirectionUpdates = GetCoordinatesDirectionUpdates(_locationService)
    }

    @After
    fun tearDown() {

    }
}
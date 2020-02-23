package eu.jakubneukirch.compass.app

import eu.jakubneukirch.compass.screen.main.MainViewModel
import eu.jakubneukirch.compass.screen.main.usecase.CoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.GetNorthDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.ICoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.IGetNorthDirectionUpdates
import eu.jakubneukirch.compass.service.AndroidLocationService
import eu.jakubneukirch.compass.service.DirectionService
import eu.jakubneukirch.compass.service.LocationService
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelsModule = module {
    viewModel { MainViewModel(get()) }
}
val servicesModule = module {
    single { DirectionService(get()) }
    single<LocationService> { AndroidLocationService(get()) }
}

val useCasesModule = module {
    single<IGetNorthDirectionUpdates> { GetNorthDirectionUpdates(get()) }
    single<ICoordinatesDirectionUpdates> { CoordinatesDirectionUpdates(get()) }
}
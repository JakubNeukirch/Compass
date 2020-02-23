package eu.jakubneukirch.compass.app

import eu.jakubneukirch.compass.screen.main.MainViewModel
import eu.jakubneukirch.compass.screen.main.usecase.GetCoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.GetNorthDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.IGetCoordinatesDirectionUpdates
import eu.jakubneukirch.compass.screen.main.usecase.IGetNorthDirectionUpdates
import eu.jakubneukirch.compass.service.AndroidLocationService
import eu.jakubneukirch.compass.service.SensorNorthDirectionService
import eu.jakubneukirch.compass.service.LocationService
import eu.jakubneukirch.compass.service.NorthDirectionService
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelsModule = module {
    viewModel { MainViewModel(get(), get()) }
}
val servicesModule = module {
    single<NorthDirectionService> { SensorNorthDirectionService(get()) }
    single<LocationService> { AndroidLocationService(get()) }
}

val useCasesModule = module {
    single<IGetNorthDirectionUpdates> { GetNorthDirectionUpdates(get()) }
    single<IGetCoordinatesDirectionUpdates> { GetCoordinatesDirectionUpdates(get(), get()) }
}
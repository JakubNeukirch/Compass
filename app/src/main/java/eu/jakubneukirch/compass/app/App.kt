package eu.jakubneukirch.compass.app

import android.app.Application
import eu.jakubneukirch.compass.screen.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            module {
                viewModel { MainViewModel() }
            }
        }
    }
}
package ci.nsu.mobile.main

import android.app.Application
import ci.nsu.mobile.main.di.ServiceLocator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация ServiceLocator при старте приложения
        ServiceLocator.getInstance(this)
    }
}
package ademar.template

import ademar.template.arch.ArchBinder
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    // creates arch binder before any activity
    @Inject lateinit var archBinder: ArchBinder

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}

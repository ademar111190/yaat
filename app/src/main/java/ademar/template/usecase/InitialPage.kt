package ademar.template.usecase

import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

enum class InitialPage {
    DEFAULT,
    DASHBOARD,
    STOCKS,
    SETTINGS,
}

@Reusable
class InitialPagePreference @Inject constructor(
    @ApplicationContext context: Context,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    private val preference = context.getSharedPreferences("page_preferences", Context.MODE_PRIVATE)

    fun set(page: InitialPage) {
        preference.edit()
            .putString("initial_page", page.name)
            .apply()
    }

    fun get(): Single<InitialPage> {
        return Single.just("initial_page")
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map { key ->
                val def = InitialPage.DEFAULT.name
                preference.getString(key, def) ?: def
            }
            .map { name ->
                InitialPage.values().firstOrNull { it.name == name } ?: InitialPage.DEFAULT
            }
    }

}

package ademar.template.di

import ademar.template.db.AppDatabase
import ademar.template.db.AppDatabaseCreator
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(SingletonComponent::class)]
object SingletonModule {

    @[Provides Singleton]
    fun providesDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "template-database",
    ).addCallback(AppDatabaseCreator()).build()

    @[Provides Singleton QualifiedScheduler(IO)]
    fun providesSchedulerIo(): Scheduler = Schedulers.io()

    @[Provides Singleton QualifiedScheduler(COMPUTATION)]
    fun providesSchedulerComputation(): Scheduler = Schedulers.computation()

    @[Provides Singleton QualifiedScheduler(MAIN_THREAD)]
    fun providesSchedulerMainThread(): Scheduler = AndroidSchedulers.mainThread()

}

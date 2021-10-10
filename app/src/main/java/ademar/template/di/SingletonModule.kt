package ademar.template.di

import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @[Provides Singleton QualifiedScheduler(IO)]
    fun providesSchedulerIo(): Scheduler = Schedulers.io()

    @[Provides Singleton QualifiedScheduler(COMPUTATION)]
    fun providesSchedulerComputation(): Scheduler = Schedulers.computation()

    @[Provides Singleton QualifiedScheduler(MAIN_THREAD)]
    fun providesSchedulerMainThread(): Scheduler = AndroidSchedulers.mainThread()

}

package ademar.template.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewWithFragmentComponent

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(ViewWithFragmentComponent::class)]
object ViewWithFragmentModule

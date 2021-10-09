package ademar.template.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(ViewComponent::class)]
object ViewModule

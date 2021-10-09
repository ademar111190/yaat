package ademar.template.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(ActivityComponent::class)]
object ActivityModule {

    @Provides fun providesCompatActivity(impl: Activity) = impl as AppCompatActivity

    @Provides fun providesFragmentManager(app: AppCompatActivity) = app.supportFragmentManager

}

package ademar.template.di

import ademar.template.di.FragmentModule.Declarations
import ademar.template.page.dashboard.DashboardFragment
import ademar.template.page.settings.SettingsFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ademar.template.page.dashboard.Contract as DashboardContract
import ademar.template.page.settings.Contract as SettingsContract

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@Module(includes = [Declarations::class])
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @[Module InstallIn(FragmentComponent::class)]
    interface Declarations {

        @Binds fun bindDashboardView(impl: DashboardFragment): DashboardContract.View

        @Binds fun bindSettingsView(impl: SettingsFragment): SettingsContract.View

    }

}

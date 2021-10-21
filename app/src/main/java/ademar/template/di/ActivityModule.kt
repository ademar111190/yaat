package ademar.template.di

import ademar.template.di.ActivityModule.Declarations
import ademar.template.page.stocksearch.StockSearchActivity
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ademar.template.page.stocksearch.Contract as StockSearchContract

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@Module(includes = [Declarations::class])
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides fun providesCompatActivity(impl: Activity) = impl as AppCompatActivity

    @Provides fun providesFragmentManager(app: AppCompatActivity) = app.supportFragmentManager

    @[Module InstallIn(ActivityComponent::class)]
    interface Declarations {

        @Binds fun bindStockSearchView(impl: StockSearchActivity): StockSearchContract.View

    }

}

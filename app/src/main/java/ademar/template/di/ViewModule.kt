package ademar.template.di


import ademar.template.di.ViewModule.Declarations
import ademar.template.page.stocks.tile.Contract
import ademar.template.page.stocks.tile.StockTileView
import dagger.Binds
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
@Module(includes = [Declarations::class])
@InstallIn(ViewComponent::class)
object ViewModule {

    @[Module InstallIn(ViewComponent::class)]
    interface Declarations {

        @Binds fun bindStockTile(impl: StockTileView): Contract.View

    }

}

package ademar.template.di

import ademar.template.di.qualifiers.QualifiedRetrofit
import ademar.template.di.qualifiers.QualifiedRetrofitOption.ALPHA_VANTAGE
import ademar.template.network.api.AlphaVantageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(SingletonComponent::class)]
object ApiServiceModule {

    @[Provides Singleton]
    fun providesAlphaVantageService(
        @QualifiedRetrofit(ALPHA_VANTAGE) retrofit: Retrofit,
    ): AlphaVantageService = retrofit.create(AlphaVantageService::class.java)

}

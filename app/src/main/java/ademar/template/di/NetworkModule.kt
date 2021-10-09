package ademar.template.di

import ademar.template.BuildConfig
import ademar.template.di.qualifiers.QualifiedRetrofit
import ademar.template.di.qualifiers.QualifiedRetrofitOption.ALPHA_VANTAGE
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * SingletonComponent @Singleton
 * ActivityRetainedComponent @ActivityRetainedScoped // ServiceComponent @ServiceScoped
 * ActivityComponent @ActivityScoped
 * FragmentComponent @FragmentScoped // ViewComponent @ViewScoped
 * ViewWithFragmentComponent @ViewScoped
 */
@[Module InstallIn(SingletonComponent::class)]
object NetworkModule {

    @[Provides Singleton]
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .setLevel(if (BuildConfig.DEBUG) HEADERS else NONE)

    @[Provides Singleton]
    fun providesOkHttpClient(
        logging: HttpLoggingInterceptor,
    ) = OkHttpClient.Builder().addInterceptor(logging).build()

    @[Provides Singleton]
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @[Provides Singleton]
    fun providesRxJava3CallAdapterFactory(): RxJava3CallAdapterFactory = RxJava3CallAdapterFactory.create()

    @[Provides Singleton]
    fun providesMoshiConverterFactory(
        moshi: Moshi,
    ): MoshiConverterFactory = MoshiConverterFactory.create(moshi).asLenient()

    @[Provides Singleton QualifiedRetrofit(ALPHA_VANTAGE)]
    fun providesAlphaVantageRetrofit(
        client: OkHttpClient,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BuildConfig.ALPHA_VANTAGE_URL)
        .addCallAdapterFactory(rxJava3CallAdapterFactory)
        .addConverterFactory(moshiConverterFactory)
        .build()

}

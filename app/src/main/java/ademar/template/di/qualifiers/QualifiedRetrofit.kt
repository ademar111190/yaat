package ademar.template.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class QualifiedRetrofit(val option: QualifiedRetrofitOption)

enum class QualifiedRetrofitOption {
    ALPHA_VANTAGE
}

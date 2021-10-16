package ademar.template.arch

import io.reactivex.rxjava3.core.Observable

interface ArchErrorMapper<T> {

    fun mapError(error: Throwable): Observable<T>

    class Impl<T>(
        private val stateFactory: (String) -> T,
    ) : ArchErrorMapper<T> {

        override fun mapError(error: Throwable): Observable<T> {
            return Observable.just(
                stateFactory(
                    error.localizedMessage ?: error.message ?: "$error",
                ),
            )
        }

    }

}

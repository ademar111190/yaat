package ademar.template.arch

import io.reactivex.rxjava3.core.Observable

interface ArchErrorMapper<State : Any> {

    fun mapError(error: Throwable): Observable<State>

    class Impl<State : Any>(
        private val stateFactory: (String) -> State,
    ) : ArchErrorMapper<State> {

        override fun mapError(error: Throwable): Observable<State> {
            return Observable.just(
                stateFactory(
                    error.localizedMessage ?: error.message ?: "$error",
                ),
            )
        }

    }

}

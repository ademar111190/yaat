package ademar.template.ext

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.error
import io.reactivex.rxjava3.core.Single.just
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

inline fun <reified T> Subject<T>.valueOrError(): Single<T> {
    if (this is BehaviorSubject<T>) {
        val lastValue = this.value ?: return error(Exception("No last value"))
        return just(lastValue)
    } else {
        return error(Exception("Not BehaviorSubject"))
    }
}

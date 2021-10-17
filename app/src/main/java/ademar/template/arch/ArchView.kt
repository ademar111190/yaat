package ademar.template.arch

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.Subject

interface ArchView<Model : Any, Command : Any> {

    val subscriptions: CompositeDisposable

    val output: Subject<Command>

    fun render(model: Model)

}

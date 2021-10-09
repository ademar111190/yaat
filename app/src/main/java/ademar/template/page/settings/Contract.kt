package ademar.template.page.settings

import io.reactivex.rxjava3.subjects.Subject

interface Contract {

    interface View {

        val output: Subject<Command>

    }

    sealed class Command {

        object Start : Command()

    }

}
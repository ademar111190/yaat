package ademar.template.page.home

import io.reactivex.rxjava3.subjects.Subject

interface Contract {

    interface View {

        val output: Subject<Command>

    }

    sealed class Command {

        object Start : Command()

        object SelectDashboard : Command()

        object SelectSettings : Command()

    }

}

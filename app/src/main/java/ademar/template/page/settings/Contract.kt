package ademar.template.page.settings

import ademar.template.arch.ArchView
import ademar.template.usecase.InitialPage

interface Contract {

    interface View : ArchView<Model, Command>

    sealed class State {

        data class DataState(
            val initialPageSelected: InitialPage,
            val initialPageOptions: List<InitialPage>,
        ) : State()

        data class ErrorState(
            val message: String,
        ) : State()

    }

    sealed class Command {

        object Initial : Command()

        data class ChangeInitialPage(
            val newPage: InitialPage,
        ) : Command()

    }

    sealed class Model {

        object Loading : Model()

        data class Error(
            val message: String,
        ) : Model()

        data class DataModel(
            val initialPageTitle: String,
            val initialPageValue: String,
            val initialPageOptions: Map<String, InitialPage>,
        ) : Model()

    }

}

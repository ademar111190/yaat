package ademar.template.page.stocks

import ademar.template.arch.ArchView

interface Contract {

    interface View : ArchView<Model, Command>

    sealed class Command {

        object Initial : Command()

        object Search : Command()

    }

    sealed class State {

        data class DataState(
            val symbols: List<Item>,
        ) : State()

        data class ErrorState(
            val message: String,
        ) : State()

    }

    sealed class Model {

        object Loading : Model()

        data class Error(
            val message: String,
        ) : Model()

        data class Empty(
            val message: String,
        ) : Model()

        data class DataModel(
            val items: List<Item>,
        ) : Model()

    }

    data class Item(
        val symbol: String,
        val value: Double,
    )

}

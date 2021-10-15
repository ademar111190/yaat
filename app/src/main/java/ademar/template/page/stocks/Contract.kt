package ademar.template.page.stocks

import io.reactivex.rxjava3.subjects.Subject

interface Contract {

    interface View {

        val output: Subject<Command>

    }

    sealed class Command {

        object Initial : Command()

        object Search : Command()

    }

    data class State(
        val symbols: List<Item>,
    )

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

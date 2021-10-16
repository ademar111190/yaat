package ademar.template.page.stocks.tile

import io.reactivex.rxjava3.subjects.Subject

interface Contract {

    interface View {

        val output: Subject<Command>

    }

    sealed class Command {

        object Initial : Command()

        object Retry : Command()

        data class Bind(
            val symbol: String,
        ) : Command()

    }

    sealed class State {

        object NoSymbol : State()

        data class InquiryState(
            val symbol: String,
        ) : State()

        data class DataState(
            val symbol: String,
            val value: Double,
        ) : State()

    }

    sealed class Model {

        object Loading : Model()

        data class Error(
            val message: String,
            val retry: String,
        ) : Model()

        data class DataModel(
            val symbol: String,
            val value: String,
        ) : Model()

    }

}

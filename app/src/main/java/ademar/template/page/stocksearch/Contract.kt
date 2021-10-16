package ademar.template.page.stocksearch

import io.reactivex.rxjava3.subjects.Subject

interface Contract {

    interface View {

        val output: Subject<Command>

    }

    sealed class Command {

        object Initial : Command()

        data class Search(
            val term: String,
        ) : Command()

        data class VoiceSearch(
            val term: String,
        ) : Command()

    }

    sealed class State {

        object NoSearch : State()

        object Searching : State()

        data class SearchResult(
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
        val name: String,
        val type: String,
    )

}

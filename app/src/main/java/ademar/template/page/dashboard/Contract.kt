package ademar.template.page.dashboard

import ademar.template.arch.ArchView

interface Contract {

    interface View : ArchView<Model, Command>

    sealed class Command {

        object Initial : Command()

        object Reload : Command()

    }

    sealed class State {

        data class DataState(
            val progressPercent: Double,
            val difficultyChange: Double,
            val estimatedRetargetDate: Double,
            val remainingBlocks: Int,
            val remainingTime: Double,
            val previousRetarget: Double,
        ) : State()

        data class ErrorState(
            val message: String,
        ) : State()

    }

    sealed class Model {

        data class DataModel(
            val progressPercentTitle: String,
            val progressPercent: String,
            val difficultyChangeTitle: String,
            val difficultyChange: String,
            val estimatedRetargetDateTitle: String,
            val estimatedRetargetDate: String,
            val remainingBlocksTitle: String,
            val remainingBlocks: String,
            val remainingTimeTitle: String,
            val remainingTime: String,
            val previousRetargetTitle: String,
            val previousRetarget: String,
        ) : Model()

        data class Error(
            val message: String,
        ) : Model()

    }

}

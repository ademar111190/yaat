package ademar.template.usecase

import ademar.template.db.AppDatabase
import ademar.template.db.DifficultyAdjustmentEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import dagger.Reusable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

@Reusable
class SaveDifficultyAdjustment @Inject constructor(
    private val db: AppDatabase,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    operator fun invoke(
        progressPercent: Double,
        difficultyChange: Double,
        estimatedRetargetDate: Double,
        remainingBlocks: Int,
        remainingTime: Double,
        previousRetarget: Double,
    ): Completable = db.difficultyAdjustmentDao()
        .insert(
            DifficultyAdjustmentEntity(
                id = 1,
                progressPercent = progressPercent,
                difficultyChange = difficultyChange,
                estimatedRetargetDate = estimatedRetargetDate,
                remainingBlocks = remainingBlocks,
                remainingTime = remainingTime,
                previousRetarget = previousRetarget,
            )
        )
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

}

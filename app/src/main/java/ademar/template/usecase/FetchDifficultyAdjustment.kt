package ademar.template.usecase

import ademar.template.db.AppDatabase
import ademar.template.db.DifficultyAdjustmentEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.network.api.MempoolSpaceService
import ademar.template.network.payload.DifficultyAdjustmentResponse
import dagger.Reusable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@Reusable
class FetchDifficultyAdjustment @Inject constructor(
    private val db: AppDatabase,
    private val service: MempoolSpaceService,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    fun local(): Single<DifficultyAdjustmentEntity> = db.difficultyAdjustmentDao()
        .get()
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

    fun remote(): Single<DifficultyAdjustmentResponse> = service
        .difficultyAdjustment()
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

}

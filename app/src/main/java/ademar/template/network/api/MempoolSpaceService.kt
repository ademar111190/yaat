package ademar.template.network.api

import ademar.template.network.payload.DifficultyAdjustmentResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface MempoolSpaceService {

    @GET("/api/v1/difficulty-adjustment")
    fun difficultyAdjustment(): Single<DifficultyAdjustmentResponse>

}

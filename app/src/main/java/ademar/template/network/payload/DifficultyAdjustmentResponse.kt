package ademar.template.network.payload

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DifficultyAdjustmentResponse(
    @Json(name = "progressPercent") val progressPercent: Double?,
    @Json(name = "difficultyChange") val difficultyChange: Double?,
    @Json(name = "estimatedRetargetDate") val estimatedRetargetDate: Double?,
    @Json(name = "remainingBlocks") val remainingBlocks: Int?,
    @Json(name = "remainingTime") val remainingTime: Double?,
    @Json(name = "previousRetarget") val previousRetarget: Double?,
)

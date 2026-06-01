package ci.nsu.mobile.main.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    @SerialName("message")
    val message: String,
    @SerialName("errors")
    val errors: Map<String, List<String>>? = null
)
package model

import kotlinx.serialization.Serializable

@Serializable
data class Spot(
    val number: Int,
    val available: Boolean,
    val status: Status? = null,
    val utenteId: Int?=null,
) {
    enum class Status {
        ENTRY, EXIT
    }
}
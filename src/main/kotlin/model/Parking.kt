package model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class Parking(
    val id: Int,
    val entranceTime: String,
    val utente: Utente?,
    val utenteId: Int?,
    val spot: Spot?,
)
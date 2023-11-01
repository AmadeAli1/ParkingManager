package model

import kotlinx.serialization.Serializable

@Serializable
data class Utente(
    val email: String,
    val id: Int,
    val nome: String,
    val password: String,
    val paymentType: String,
    val type: String,
    val divida: Float,
    val lastSubscription: String?,
)
package model

data class Payment(
    val amount: Double,
    val amountPerHour: Int,
    val discount: Double,
    val entranceTime: String,
    val exitTime: String,
    val id: Int,
    val timeInParking: String,
    val utenteId: Int
)
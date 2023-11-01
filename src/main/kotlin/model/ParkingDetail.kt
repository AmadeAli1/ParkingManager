package model

data class ParkingDetail(
    val amount: Double,
    val amountPerHour: Int,
    val discount: Double,
    val entranceTime: String,
    val exitTime: String,
    val id: Any,
    val timeInParking: String,
    val utenteId: Int
)
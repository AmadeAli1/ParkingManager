package viewmodel

import model.ParkingDetail

data class UiState(
    val isClosed: Boolean = false,
    val available: Int = 20,
    val occupied: Int = 0,
)

data class ScannerState(
    val utenteId: Int? = null,
    val isLoading: Boolean = false,
    val parkingDetail: ParkingDetail? = null,
    val errorMessage: String? = null,
)


sealed class PaymentState {
    object Hide : PaymentState()
    object Loading : PaymentState()
    object Success : PaymentState()
    data class Show(val parkingDetail: ParkingDetail) : PaymentState()
    data class Failure(val message: String) : PaymentState()
}
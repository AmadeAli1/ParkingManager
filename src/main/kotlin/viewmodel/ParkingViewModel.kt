package viewmodel

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import model.ParkingSpot
import model.Spot
import repository.ParkingRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ParkingViewModel {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataSource = ParkingRepository()
    val slots = mutableStateMapOf<Int, ParkingSpot>()
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(10_000),
        initialValue = UiState()
    )

    private val _clockState = MutableStateFlow(Clock())
    val clockState = _clockState.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(20_000),
        initialValue = Clock()
    )


    private val _scannerState = MutableStateFlow<ScannerState>(ScannerState())
    val scannerState = _scannerState.stateIn(
        scope = coroutineScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = ScannerState()
    )


    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Hide)
    val paymentState = _paymentState.stateIn(
        scope = coroutineScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = PaymentState.Hide
    )


    init {
        initSlots()
        coroutineScope.launch {
            try {
                dataSource.parkingStateFlow().collect {
                    when (it.spot?.status) {
                        Spot.Status.EXIT -> {
                            val parkingSpot = slots[it.spot.number]
                            if (parkingSpot != null) {
                                slots[it.spot.number] = parkingSpot.copy(
                                    spot = Spot(number = it.spot.number, available = true, status = Spot.Status.EXIT)
                                )
                            }
                        }

                        Spot.Status.ENTRY -> {
                            slots[it.spot.number] = ParkingSpot(it.spot, it)
                        }

                        else -> Unit
                    }
                    parkingInfo()
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }

        coroutineScope.launch(Dispatchers.Default) {
            val after = LocalTime.of(23, 0, 0)
            val before = LocalTime.of(6, 0, 0)
            while (true) {
                val now = LocalTime.now()
                if (now.isAfter(after) || now.isBefore(before)) {
                    _uiState.emit(_uiState.value.copy(isClosed = true))
                } else {
                    _uiState.emit(_uiState.value.copy(isClosed = false))
                }
                delay(5_000)
            }
        }

        coroutineScope.launch {
            while (true) {
                changeDayAndTime()
            }
        }

    }

    private suspend fun parkingInfo() {
        val available = slots.count { it.value.spot.available }
        val occupied = slots.count { !it.value.spot.available }
        _uiState.emit(_uiState.value.copy(available = available, occupied = occupied))
    }

    private fun paymentDetails() {
        coroutineScope.launch {
            _scannerState.value.utenteId?.let {
                dataSource.parkingDetails(utenteId = it, onLoading = {
                    _scannerState.tryEmit(_scannerState.value.copy(isLoading = true))
                }, onSuccess = { detail ->
                    _scannerState.tryEmit(_scannerState.value.copy(isLoading = false, parkingDetail = detail))
                }, onFailure = { message ->
                    _scannerState.tryEmit(_scannerState.value.copy(isLoading = false, errorMessage = message))
                })
            }

        }
    }

    private suspend fun changeDayAndTime() {
        val day = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM uuuu"))
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"))
        _clockState.emit(Clock(day, time))
    }

    private fun initSlots() {
        println(slots.size)
        repeat(20) {
            val parkingSpot = ParkingSpot(Spot(available = true, number = it + 1, status = Spot.Status.EXIT))
            slots[it + 1] = parkingSpot
        }
    }

    fun readQrCode(code: String) {
        coroutineScope.launch {
            if (code.toIntOrNull() != null) {
                _scannerState.emit(ScannerState(code.toInt()))
                delay(1000)
                paymentDetails()
            }
        }
    }

    fun clear() {
        coroutineScope.launch {
            _scannerState.emit(ScannerState())
            _paymentState.emit(PaymentState.Hide)
        }
    }

    fun pay() {
        coroutineScope.launch {
            _scannerState.value.utenteId?.let {
                dataSource.pay(utenteId = it, onSuccess = { data ->
                    scannerState.value.parkingDetail?.let { it1 -> PaymentState.Show(it1) }
                        ?.let { it2 -> _paymentState.tryEmit(it2) }
                    println("Data")
                }, onFailure = { message ->
                    _paymentState.tryEmit(PaymentState.Failure(message))
                })
            }
        }
    }

}
package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import repository.RetrofitInstance
import retrofit2.create
import service.ParkingService
import java.io.IOException

class DashboardViewModel {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    private val service: ParkingService = RetrofitInstance.retrofit.create()

    private val _paymentChart = MutableStateFlow(emptyMap<String,Double>())
    val paymentChart = _paymentChart.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init {
        findAll()
    }

    private fun findAll() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = service.findAllPayments()
                delay(500)
                if (response.isSuccessful) {
                    val payments = response.body()!!
                    val groupBy = payments.groupBy {
                        it.entranceTime.split(" ")[2]
                    }.map { map ->
                        Pair(map.key,map.value.sumOf { it.amount })
                    }
                    _paymentChart.emit(groupBy.toMap())
                } else {
                    //response.errorBody()?.toMessage()?.let { onFailure(it) }
                }
            } catch (io: IOException) {
                //onFailure("Check your internet connection")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    data class PaymentChart(
        val month: String,
        val amount: Double,
    )

}
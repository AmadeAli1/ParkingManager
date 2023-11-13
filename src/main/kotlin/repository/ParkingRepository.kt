package repository

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Parking
import model.ParkingDetail
import retrofit2.create
import service.ParkingService
import java.io.IOException

class ParkingRepository {
    private val service by lazy { RetrofitInstance.retrofit.create<ParkingService>() }
    private val wss: HttpClient by lazy {
        HttpClient(CIO) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    private var session: WebSocketSession? = null


    fun parkingStateFlow(): Flow<Parking> {
        return flow {
            session = wss.webSocketSession {
                url("ws://localhost:8080/api/ws/parking")
            }
            val state = session!!.incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull {
                    val readText = it.readText()
                    Json.decodeFromString<Parking>(readText)
                }
            emitAll(state)
        }.retryWhen { _, _ ->
            delay(1000)
            return@retryWhen true
        }
    }


    suspend fun parkingDetails(
        utenteId: Int,
        onLoading: () -> Unit,
        onSuccess: (ParkingDetail) -> Unit,
        onFailure: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            onLoading()
            val response = service.parkingPaymentDetails(utenteId)
            delay(1000)
            if (response.isSuccessful) {
                response.body()?.let(onSuccess)
            } else {
                response.errorBody()?.toMessage()?.let { onFailure(it) }
            }
        } catch (e: Exception) {
            e.cause?.let {
                it.message?.let { it1 -> onFailure(it1) }
            }
        }
    }

    suspend fun pay(
        utenteId: Int,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) =
        withContext(Dispatchers.IO) {
            try {
                val response = service.pay(utenteId)
                delay(500)
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it.message) }
                } else {
                    response.errorBody()?.toMessage()?.let { onFailure(it) }
                }
            } catch (io: IOException) {
                onFailure("Check your internet connection")
            } catch (e: Exception) {
                e.message?.let(onFailure)
            }
        }


}
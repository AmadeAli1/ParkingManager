package service

import model.ParkingDetail
import model.Payment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingService {
    @GET("parking/payment/detail")
    suspend fun parkingPaymentDetails(@Query("utenteId") id: Int): Response<ParkingDetail>

    @GET("parking/payment/confirm")
    suspend fun pay(@Query("utenteId") id: Int): Response<ApiResponse<Unit>>

    @GET("parking/payment/all")
    suspend fun findAllPayments(): Response<List<Payment>>
}
package service

import model.Utente
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UtenteService {

    @GET("utente/all")
    suspend fun findAll(): Response<List<Utente>>

    @GET("utente/query")
    suspend fun searchByName(@Query("nome") name: String): Response<List<Utente>>

    @GET("utente/findOne")
    suspend fun findById(@Query("id") utenteId: Int): Response<Utente>

}
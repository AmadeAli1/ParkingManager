package repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}
package com.polyblack.retrofitfix.network

import com.polyblack.retrofitfix.model.CatFactResponse
import retrofit2.http.GET

interface ApiService {

    @GET("/fact")
    suspend fun getSomethingFromApi(): CatFactResponse
}

package com.example.proyectomapasgeo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OpenRouteServiceApi {
    @Headers("Authorization: Bearer 5b3ce3597851110001cf6248ef9bab3f38fc40808836c86b76b42270")
    @GET("v2/directions/driving-car")
    fun getRoute(
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<RouteResponse>
}

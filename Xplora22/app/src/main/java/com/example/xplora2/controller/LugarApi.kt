package com.example.xplora2.controller

import com.example.xplora2.model.Lugar
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LugarApi {
    @GET("api/lugares")
    fun getLugares(): Call<List<Lugar>>

    @GET("api/lugares/{id}")
    fun obtenerLugarPorId(@Path("id") id: String): Call<Lugar>

    @POST("api/lugares")
    fun agregarLugar(@Body lugar: Lugar): Call<Lugar>

    @PUT("api/lugares/{id}")
    fun updateLugar(@Path("id") id: String, @Body lugar: Lugar): Call<Lugar>

    @DELETE("api/lugares/{id}")
    fun eliminarLugar(@Path("id") id: String): Call<Void>
}
package com.example.xplora2

import retrofit2.Call
import retrofit2.http.*

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
package com.example.androidnative.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuarioApiClient {

    private const val BASE_URL = "http://127.0.0.1:8080/"

    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApiService by lazy {
        retrofit.create(UsuarioApiService::class.java)
    }
}
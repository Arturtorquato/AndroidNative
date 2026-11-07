package com.example.androidnative.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApiService {

    @POST("usuarios")
    suspend fun cadastrar(
        @Body usuario: UsuarioCadastroRequest
    ): Response<UsuarioResponse>

    @POST("auth/login")
    suspend fun login(
        @Body login: LoginRequest
    ): Response<UsuarioResponse>
}
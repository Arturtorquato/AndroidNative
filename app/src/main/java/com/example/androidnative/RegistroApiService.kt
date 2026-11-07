package com.example.androidnative.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RegistroApiService {

    @GET("registros")
    suspend fun listar(): Response<List<RegistroResponse>>

    @GET("registros/{id}")
    suspend fun buscarPorId(
        @Path("id") id: Long
    ): Response<RegistroResponse>

    @POST("registros")
    suspend fun inserir(
        @Body registro: RegistroRequest
    ): Response<RegistroResponse>

    @PUT("registros/{id}")
    suspend fun atualizar(
        @Path("id") id: Long,
        @Body registro: RegistroRequest
    ): Response<RegistroResponse>

    @DELETE("registros/{id}")
    suspend fun excluir(
        @Path("id") id: Long
    ): Response<Unit>
}
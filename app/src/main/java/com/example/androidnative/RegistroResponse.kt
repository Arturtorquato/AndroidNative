package com.example.androidnative.api

data class RegistroResponse(
    val id: Long,
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val valor: Double,
    val quantidade: Int
)
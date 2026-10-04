package com.example.androidnative.api

data class RegistroRequest(
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val valor: Double,
    val quantidade: Int
)
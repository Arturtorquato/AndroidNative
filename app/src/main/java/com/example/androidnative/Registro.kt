package com.example.androidnative

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class Registro(

    @PrimaryKey
    val id: Long,

    val titulo: String,
    val descricao: String,
    val categoria: String,
    val valor: Double,
    val quantidade: Int
)
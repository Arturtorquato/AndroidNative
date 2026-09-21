package com.example.androidnative

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class Registro(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titulo: String,
    val descricao: String,
    val categoria: String,
    val valor: Double,
    val quantidade: Int
)
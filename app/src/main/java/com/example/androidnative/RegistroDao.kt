package com.example.androidnative

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert
    suspend fun inserir(registro: Registro)

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun listar(): Flow<List<Registro>>

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun buscarPorId(id: Int): Registro?

    @Update
    suspend fun atualizar(registro: Registro)

    @Delete
    suspend fun excluir(registro: Registro)

    @Query("DELETE FROM registros")
    suspend fun excluirTodos()
}
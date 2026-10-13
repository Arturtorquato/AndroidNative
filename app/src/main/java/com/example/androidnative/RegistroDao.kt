package com.example.androidnative

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: Registro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(registros: List<Registro>)

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun listar(): Flow<List<Registro>>

    @Query("SELECT * FROM registros ORDER BY id DESC")
    suspend fun listarSnapshot(): List<Registro>

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun buscarPorId(id: Long): Registro?

    @Query("""
        SELECT * FROM registros
        WHERE (
            :texto = ''
            OR LOWER(titulo) LIKE '%' || LOWER(:texto) || '%'
            OR LOWER(descricao) LIKE '%' || LOWER(:texto) || '%'
            OR LOWER(categoria) LIKE '%' || LOWER(:texto) || '%'
        )
        AND (:categoria = 'Todas' OR categoria = :categoria)
        AND (:valorMinimo IS NULL OR valor >= :valorMinimo)
        AND (:valorMaximo IS NULL OR valor <= :valorMaximo)
        ORDER BY id DESC
    """)
    suspend fun pesquisar(
        texto: String,
        categoria: String,
        valorMinimo: Double?,
        valorMaximo: Double?
    ): List<Registro>

    @Query("SELECT COUNT(*) FROM registros")
    fun contarRegistros(): Flow<Int>

    @Update
    suspend fun atualizar(registro: Registro)

    @Delete
    suspend fun excluir(registro: Registro)

    @Query("DELETE FROM registros")
    suspend fun excluirTodos()
}
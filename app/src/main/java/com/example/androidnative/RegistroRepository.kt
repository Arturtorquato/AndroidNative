package com.example.androidnative.repository

import com.example.androidnative.AppDatabase
import com.example.androidnative.Registro
import com.example.androidnative.api.ApiClient
import com.example.androidnative.api.RegistroResponse
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class RegistroRepository(
    private val database: AppDatabase
) {

    private val registroDao = database.registroDao()

    private val registroApi = ApiClient.registroApi

    fun observarRegistros(): Flow<List<Registro>> {
        return registroDao.listar()
    }

    suspend fun sincronizarRegistros() {

        // Busca todos os registros do Spring Boot.
        val resposta = registroApi.listar()

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao consultar API: HTTP ${resposta.code()}"
            )
        }

        val registrosRemotos = resposta.body()
            ?: throw IOException("A API retornou uma resposta vazia")

        // Converte os objetos da API para entidades Room.
        val registrosLocais = registrosRemotos.map { resposta ->
            resposta.paraRegistro()
        }

        // Atualiza a cópia local em uma única transação.
        database.withTransaction {

            registroDao.excluirTodos()

            registroDao.inserirTodos(registrosLocais)
        }
    }

    suspend fun buscarLocalPorId(id: Long): Registro? {
        return registroDao.buscarPorId(id)
    }

    private fun RegistroResponse.paraRegistro(): Registro {

        return Registro(
            id = id,
            titulo = titulo,
            descricao = descricao,
            categoria = categoria,
            valor = valor,
            quantidade = quantidade
        )
    }
}
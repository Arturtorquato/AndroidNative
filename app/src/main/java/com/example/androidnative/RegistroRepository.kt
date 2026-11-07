package com.example.androidnative.repository

import androidx.room.withTransaction
import com.example.androidnative.AppDatabase
import com.example.androidnative.Registro
import com.example.androidnative.api.ApiClient
import com.example.androidnative.api.RegistroRequest
import com.example.androidnative.api.RegistroResponse
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

    
    suspend fun cadastrarRegistro(
        titulo: String,
        descricao: String,
        categoria: String,
        valor: Double,
        quantidade: Int
    ): Registro {

        val request = RegistroRequest(
            titulo = titulo,
            descricao = descricao,
            categoria = categoria,
            valor = valor,
            quantidade = quantidade
        )

        val resposta = registroApi.inserir(request)

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao cadastrar: HTTP ${resposta.code()}"
            )
        }

        val remoto = resposta.body()
            ?: throw IOException("A API não retornou o registro")

        val registro = remoto.paraRegistro()

        registroDao.inserir(registro)

        return registro
    }

    
    suspend fun buscarRegistro(id: Long): Registro {

        val resposta = registroApi.buscarPorId(id)

        if (resposta.code() == 404) {
            throw NoSuchElementException(
                "Registro não encontrado"
            )
        }

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao consultar: HTTP ${resposta.code()}"
            )
        }

        val remoto = resposta.body()
            ?: throw IOException("Resposta vazia da API")

        val registro = remoto.paraRegistro()

        registroDao.inserir(registro)

        return registro
    }

    
    suspend fun buscarLocalPorId(id: Long): Registro? {
        return registroDao.buscarPorId(id)
    }

    
    suspend fun editarRegistro(
        id: Long,
        titulo: String,
        descricao: String,
        categoria: String,
        valor: Double,
        quantidade: Int
    ): Registro {

        val request = RegistroRequest(
            titulo = titulo,
            descricao = descricao,
            categoria = categoria,
            valor = valor,
            quantidade = quantidade
        )

        val resposta = registroApi.atualizar(id, request)

        if (resposta.code() == 404) {
            throw NoSuchElementException(
                "Registro não encontrado no servidor"
            )
        }

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao atualizar: HTTP ${resposta.code()}"
            )
        }

        val remoto = resposta.body()
            ?: throw IOException(
                "A API não retornou o registro atualizado"
            )

        val registro = remoto.paraRegistro()

        registroDao.inserir(registro)

        return registro
    }

    
    suspend fun excluirRegistro(id: Long) {

        val resposta = registroApi.excluir(id)

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao excluir: HTTP ${resposta.code()}"
            )
        }

        val registroLocal = registroDao.buscarPorId(id)

        if (registroLocal != null) {
            registroDao.excluir(registroLocal)
        }
    }

    
    suspend fun sincronizarRegistros() {

        val resposta = registroApi.listar()

        if (!resposta.isSuccessful) {
            throw IOException(
                "Erro ao sincronizar: HTTP ${resposta.code()}"
            )
        }

        val remotos = resposta.body()
            ?: throw IOException("Resposta vazia da API")

        val locais = remotos.map {
            it.paraRegistro()
        }

        database.withTransaction {

            registroDao.excluirTodos()

            registroDao.inserirTodos(locais)
        }
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
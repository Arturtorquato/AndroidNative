package com.example.androidnative

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidnative.api.ApiClient
import kotlinx.coroutines.launch
import java.io.IOException

class ApiTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val texto = TextView(this).apply {
            text = "Consultando API..."
            textSize = 18f
            setPadding(32, 64, 32, 32)
        }

        setContentView(texto)

        lifecycleScope.launch {
            try {
                val resposta = ApiClient.registroApi.listar()

                if (resposta.isSuccessful) {
                    val registros = resposta.body().orEmpty()

                    texto.text = buildString {
                        appendLine("Conexão realizada com sucesso!")
                        appendLine("Total de registros: ${registros.size}")
                        appendLine()

                        registros.forEach { registro ->
                            appendLine("ID: ${registro.id}")
                            appendLine("Título: ${registro.titulo}")
                            appendLine("Categoria: ${registro.categoria}")
                            appendLine()
                        }
                    }
                } else {
                    texto.text = "Erro HTTP: ${resposta.code()}"
                }

            } catch (e: IOException) {
                texto.text = "Falha de conexão: ${e.message}"

            } catch (e: Exception) {
                texto.text = "Erro: ${e.message}"
            }
        }
    }
}
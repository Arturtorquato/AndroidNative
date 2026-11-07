
package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.androidnative.repository.RegistroRepository

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListaRegistrosActivity : AppCompatActivity() {

    private lateinit var adapter: RegistroAdapter
    private lateinit var repository: RegistroRepository

    private var registros = emptyList<Registro>()
    private var textoPesquisa = ""

    companion object {
        private const val TAG_DESEMPENHO = "TCC_DESEMPENHO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_registros)

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.recyclerRegistros)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                0
            )

            insets
        }

        val edtPesquisa = findViewById<EditText>(
            R.id.edtPesquisa
        )

        val btnPesquisar = findViewById<Button>(
            R.id.btnPesquisar
        )

        val btnVoltar = findViewById<Button>(
            R.id.btnVoltar
        )

        val recyclerRegistros = findViewById<RecyclerView>(
            R.id.recyclerRegistros
        )

        adapter = RegistroAdapter(emptyList()) { registro ->
            val intent = Intent(
                this@ListaRegistrosActivity,
                DetalhesRegistroActivity::class.java
            )

            intent.putExtra("id", registro.id)

            startActivity(intent)
        }

        recyclerRegistros.layoutManager = LinearLayoutManager(this)
        recyclerRegistros.adapter = adapter

        lifecycleScope.launch {
            repository.observarRegistros().collectLatest { lista ->
                registros = lista

                medirAtualizacaoLista(
                    origem = "ROOM",
                    medirDesempenho = false
                )
            }
        }

        btnPesquisar.setOnClickListener {
            textoPesquisa = edtPesquisa.text.toString().trim()

            medirAtualizacaoLista(
                origem = "PESQUISA",
                medirDesempenho = true
            )
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun medirAtualizacaoLista(
        origem: String,
        medirDesempenho: Boolean
    ) {
        val inicio = SystemClock.elapsedRealtimeNanos()

        val filtrados = if (textoPesquisa.isEmpty()) {
            registros
        } else {
            registros.filter { registro ->
                registro.titulo.contains(
                    textoPesquisa,
                    ignoreCase = true
                ) || registro.descricao.contains(
                    textoPesquisa,
                    ignoreCase = true
                ) || registro.categoria.contains(
                    textoPesquisa,
                    ignoreCase = true
                )
            }
        }

        adapter.atualizarLista(filtrados)

        val fim = SystemClock.elapsedRealtimeNanos()
        val tempoMs = (fim - inicio) / 1_000_000.0

        if (medirDesempenho) {
            Log.i(
                TAG_DESEMPENHO,
                "operacao=LISTAGEM | origem=$origem" +
                        " | totalRegistros=${registros.size}" +
                        " | resultados=${filtrados.size}" +
                        " | tempoMs=$tempoMs" +
                        " | textoPesquisa=$textoPesquisa"
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (!::repository.isInitialized) return

        lifecycleScope.launch {
            try {
                repository.sincronizarRegistros()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ListaRegistrosActivity,
                    "Não foi possível atualizar pela API. Exibindo registros locais.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

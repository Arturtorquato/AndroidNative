package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidnative.repository.RegistroRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class DetalhesRegistroActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    private var registroId: Long = -1L

    private lateinit var tvValorTitulo: TextView
    private lateinit var tvValorDescricao: TextView
    private lateinit var tvValorCategoria: TextView
    private lateinit var tvValor: TextView
    private lateinit var tvValorQuantidade: TextView

    private lateinit var btnEditar: Button
    private lateinit var btnExcluir: Button

    companion object {
        private const val TAG_DESEMPENHO = "TCC_DESEMPENHO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_registro)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        registroId = intent.getLongExtra("id", -1L)

        tvValorTitulo = findViewById(R.id.tvValorTitulo)
        tvValorDescricao = findViewById(R.id.tvValorDescricao)
        tvValorCategoria = findViewById(R.id.tvValorCategoria)
        tvValor = findViewById(R.id.tvValor)
        tvValorQuantidade = findViewById(R.id.tvValorQuantidade)

        btnEditar = findViewById(R.id.btnEditar)
        btnExcluir = findViewById(R.id.btnExcluir)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        if (registroId == -1L) {
            Toast.makeText(
                this,
                "ID do registro inválido",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        btnEditar.setOnClickListener {
            val intent = Intent(
                this,
                EditarRegistroActivity::class.java
            )

            intent.putExtra("id", registroId)
            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            confirmarExclusao()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (registroId != -1L) {
            carregarRegistro()
        }
    }

    






    private fun carregarRegistro() {
        lifecycleScope.launch {
            val inicioTotal = SystemClock.elapsedRealtimeNanos()

            try {
                val inicioConsulta = SystemClock.elapsedRealtimeNanos()

                val registro = repository.buscarRegistro(registroId)

                val fimConsulta = SystemClock.elapsedRealtimeNanos()

                exibirRegistro(registro)

                val fimTotal = SystemClock.elapsedRealtimeNanos()

                registrarDesempenho(
                    origem = "REPOSITORIO",
                    tempoConsultaMs = nanosParaMs(
                        fimConsulta - inicioConsulta
                    ),
                    tempoExibicaoMs = nanosParaMs(
                        fimTotal - fimConsulta
                    ),
                    tempoTotalMs = nanosParaMs(
                        fimTotal - inicioTotal
                    )
                )

            } catch (e: NoSuchElementException) {
                registrarErro("REGISTRO_NAO_ENCONTRADO", inicioTotal)

                Toast.makeText(
                    this@DetalhesRegistroActivity,
                    "Registro não encontrado no servidor",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: IOException) {
                val inicioFallback = SystemClock.elapsedRealtimeNanos()

                try {
                    val registroLocal =
                        repository.buscarLocalPorId(registroId)

                    val fimConsultaLocal =
                        SystemClock.elapsedRealtimeNanos()

                    if (registroLocal != null) {
                        exibirRegistro(registroLocal)

                        val fimTotal =
                            SystemClock.elapsedRealtimeNanos()

                        registrarDesempenho(
                            origem = "CACHE_LOCAL",
                            tempoConsultaMs = nanosParaMs(
                                fimConsultaLocal - inicioFallback
                            ),
                            tempoExibicaoMs = nanosParaMs(
                                fimTotal - fimConsultaLocal
                            ),
                            tempoTotalMs = nanosParaMs(
                                fimTotal - inicioTotal
                            )
                        )

                        Toast.makeText(
                            this@DetalhesRegistroActivity,
                            "Sem atualização da API. Exibindo dados locais.",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        registrarErro("CACHE_LOCAL_VAZIO", inicioTotal)

                        Toast.makeText(
                            this@DetalhesRegistroActivity,
                            "Não foi possível carregar o registro",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }

                } catch (erroLocal: Exception) {
                    registrarErro("ERRO_CACHE_LOCAL", inicioTotal)

                    Toast.makeText(
                        this@DetalhesRegistroActivity,
                        "Não foi possível carregar o registro local",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }

            } catch (e: Exception) {
                registrarErro("ERRO_GERAL", inicioTotal)

                Toast.makeText(
                    this@DetalhesRegistroActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun exibirRegistro(registro: Registro) {
        tvValorTitulo.text = registro.titulo
        tvValorDescricao.text = registro.descricao
        tvValorCategoria.text = registro.categoria

        tvValor.text = String.format(
            Locale("pt", "BR"),
            "R$ %.2f",
            registro.valor
        )

        tvValorQuantidade.text = registro.quantidade.toString()
    }

    private fun registrarDesempenho(
        origem: String,
        tempoConsultaMs: Double,
        tempoExibicaoMs: Double,
        tempoTotalMs: Double
    ) {
        Log.i(
            TAG_DESEMPENHO,
            "operacao=DETALHES" +
                    " | origem=$origem" +
                    " | registroId=$registroId" +
                    " | consultaMs=${"%.4f".format(Locale.US, tempoConsultaMs)}" +
                    " | exibicaoMs=${"%.4f".format(Locale.US, tempoExibicaoMs)}" +
                    " | totalMs=${"%.4f".format(Locale.US, tempoTotalMs)}"
        )
    }

    private fun registrarErro(
        origem: String,
        inicioTotal: Long
    ) {
        val fimTotal = SystemClock.elapsedRealtimeNanos()

        Log.w(
            TAG_DESEMPENHO,
            "operacao=DETALHES" +
                    " | origem=$origem" +
                    " | registroId=$registroId" +
                    " | erro=true" +
                    " | tempoTotalMs=${"%.4f".format(
                        Locale.US,
                        nanosParaMs(fimTotal - inicioTotal)
                    )}"
        )
    }

    private fun nanosParaMs(nanos: Long): Double {
        return nanos / 1_000_000.0
    }

    private fun confirmarExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir registro")
            .setMessage("Deseja realmente excluir este registro?")
            .setPositiveButton("Excluir") { _, _ ->
                excluirRegistro()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirRegistro() {
        btnExcluir.isEnabled = false
        btnEditar.isEnabled = false

        lifecycleScope.launch {
            try {
                repository.excluirRegistro(registroId)

                Toast.makeText(
                    this@DetalhesRegistroActivity,
                    "Registro excluído com sucesso",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetalhesRegistroActivity,
                    "Não foi possível concluir a exclusão. " +
                            "Verifique a API e sincronize antes de tentar novamente.",
                    Toast.LENGTH_LONG
                ).show()

                btnExcluir.isEnabled = true
                btnEditar.isEnabled = true
            }
        }
    }
}
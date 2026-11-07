package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.view.View

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.example.androidnative.repository.RegistroRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import java.util.Locale

class ProcessamentoActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    private lateinit var btnCalcularTotal: Button
    private lateinit var btnCalcularMedia: Button
    private lateinit var btnMaiorValor: Button
    private lateinit var btnOrdenar: Button
    private lateinit var btnProcessar: Button

    private lateinit var tvResultado: TextView
    private lateinit var tvDetalhes: TextView

    private var registros: List<Registro> = emptyList()

    private var carregando = false

    private val localeBR = Locale("pt", "BR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_processamento)

        configurarInsets()

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        btnCalcularTotal = findViewById(R.id.btnCalcularTotal)
        btnCalcularMedia = findViewById(R.id.btnCalcularMedia)
        btnMaiorValor = findViewById(R.id.btnMaiorValor)
        btnOrdenar = findViewById(R.id.btnOrdenar)
        btnProcessar = findViewById(R.id.btnProcessar)

        tvResultado = findViewById(R.id.tvResultado)
        tvDetalhes = findViewById(R.id.tvDetalhes)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        habilitarOperacoes(false)

        btnCalcularTotal.setOnClickListener {
            calcularTotal()
        }

        btnCalcularMedia.setOnClickListener {
            calcularMedia()
        }

        btnMaiorValor.setOnClickListener {
            encontrarMaiorValor()
        }

        btnOrdenar.setOnClickListener {
            ordenarRegistros()
        }

        btnProcessar.setOnClickListener {
            processamentoCompleto()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun configurarInsets() {

        val root = findViewById<View>(R.id.main)

        val esquerda = root.paddingLeft
        val superior = root.paddingTop
        val direita = root.paddingRight
        val inferior = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                esquerda + systemBars.left,
                superior + systemBars.top,
                direita + systemBars.right,
                inferior + systemBars.bottom
            )

            insets
        }
    }

    override fun onResume() {
        super.onResume()

        if (!::repository.isInitialized) return

        carregarRegistros()
    }

    private fun carregarRegistros() {

        if (carregando) return

        carregando = true

        habilitarOperacoes(false)

        tvResultado.text = "Carregando registros..."
        tvDetalhes.text = "Sincronizando dados com a API."

        lifecycleScope.launch {

            try {

                
                repository.sincronizarRegistros()

                
                registros = repository.observarRegistros().first()

                tvResultado.text = "Dados atualizados"

                tvDetalhes.text =
                    "Registros disponíveis: ${registros.size}\n" +
                            "Selecione uma operação para processar os dados."

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                
                try {

                    registros = repository.observarRegistros().first()

                    tvResultado.text = "Dados locais carregados"

                    tvDetalhes.text =
                        "Não foi possível atualizar pela API.\n" +
                                "Registros locais disponíveis: ${registros.size}"

                    Toast.makeText(
                        this@ProcessamentoActivity,
                        "Sem atualização da API. Utilizando dados locais.",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (localError: CancellationException) {

                    throw localError

                } catch (localError: Exception) {

                    registros = emptyList()

                    tvResultado.text = "Erro ao carregar registros"

                    tvDetalhes.text =
                        localError.message
                            ?: "Falha ao consultar o banco local."
                }

            } finally {

                carregando = false

                habilitarOperacoes(true)
            }
        }
    }

    private fun habilitarOperacoes(habilitar: Boolean) {

        btnCalcularTotal.isEnabled = habilitar
        btnCalcularMedia.isEnabled = habilitar
        btnMaiorValor.isEnabled = habilitar
        btnOrdenar.isEnabled = habilitar
        btnProcessar.isEnabled = habilitar
    }

    private fun calcularTotal() {

        val total = registros.sumOf {
            it.valor * it.quantidade
        }

        tvResultado.text = "Valor total"

        tvDetalhes.text = String.format(
            localeBR,
            "R$ %.2f",
            total
        )
    }

    private fun calcularMedia() {

        tvResultado.text = "Valor médio"

        if (registros.isEmpty()) {

            tvDetalhes.text = "Nenhum registro cadastrado."

            return
        }

        val media = registros.map {
            it.valor
        }.average()

        tvDetalhes.text = String.format(
            localeBR,
            "R$ %.2f",
            media
        )
    }

    private fun encontrarMaiorValor() {

        tvResultado.text = "Maior valor"

        val maior = registros.maxByOrNull {
            it.valor
        }

        if (maior == null) {

            tvDetalhes.text = "Nenhum registro cadastrado."

            return
        }

        tvDetalhes.text = String.format(
            localeBR,
            "%s - R$ %.2f",
            maior.titulo,
            maior.valor
        )
    }

    private fun ordenarRegistros() {

        tvResultado.text = "Registros ordenados"

        if (registros.isEmpty()) {

            tvDetalhes.text = "Nenhum registro cadastrado."

            return
        }

        val ordenados = registros.sortedByDescending {
            it.valor
        }

        tvDetalhes.text = ordenados.joinToString("\n") {

            String.format(
                localeBR,
                "%s - R$ %.2f",
                it.titulo,
                it.valor
            )
        }
    }

    private fun processamentoCompleto() {

        tvResultado.text = "Processamento"

        if (registros.isEmpty()) {

            tvDetalhes.text = "Nenhum registro cadastrado."

            return
        }

        val total = registros.sumOf {
            it.valor * it.quantidade
        }

        val media = registros.map {
            it.valor
        }.average()

        val maior = registros.maxByOrNull {
            it.valor
        }

        val quantidadeTotal = registros.sumOf {
            it.quantidade
        }

        tvResultado.text = "Processamento concluído"

        tvDetalhes.text = String.format(
            localeBR,
            "Quantidade de registros: %d\n" +
                    "Quantidade total de itens: %d\n" +
                    "Valor total: R$ %.2f\n" +
                    "Valor médio: R$ %.2f\n" +
                    "Maior registro: %s - R$ %.2f",
            registros.size,
            quantidadeTotal,
            total,
            media,
            maior?.titulo ?: "Nenhum",
            maior?.valor ?: 0.0
        )
    }
}
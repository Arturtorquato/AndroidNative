package com.example.androidnative

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

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

class GraficosActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    private lateinit var tvTotalRegistros: TextView
    private lateinit var tvTotalItens: TextView
    private lateinit var tvValorTotal: TextView
    private lateinit var tvValorMedio: TextView

    private lateinit var tvEletronicos: TextView
    private lateinit var tvPerifericos: TextView
    private lateinit var tvMoveis: TextView
    private lateinit var tvOutro: TextView

    private lateinit var progressEletronicos: ProgressBar
    private lateinit var progressPerifericos: ProgressBar
    private lateinit var progressMoveis: ProgressBar

    private lateinit var btnAtualizar: Button

    private var carregando = false

    private val localeBR = Locale("pt", "BR")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_graficos)

        configurarInsets()

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros)
        tvTotalItens = findViewById(R.id.tvTotalItens)
        tvValorTotal = findViewById(R.id.tvValorTotal)
        tvValorMedio = findViewById(R.id.tvValorMedio)

        
        tvEletronicos = findViewById(R.id.tvEletronicos)
        tvPerifericos = findViewById(R.id.tvPerifericos)
        tvMoveis = findViewById(R.id.tvMoveis)
        tvOutro = findViewById(R.id.tvOutro)

        
        progressEletronicos = findViewById(R.id.progressEletronicos)
        progressPerifericos = findViewById(R.id.progressPerifericos)
        progressMoveis = findViewById(R.id.progressMoveis)

        btnAtualizar = findViewById(R.id.btnAtualizar)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnAtualizar.setOnClickListener {
            carregarResultados()
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

        carregarResultados()
    }

    
    private fun carregarResultados() {

        if (carregando) return

        carregando = true
        btnAtualizar.isEnabled = false
        btnAtualizar.text = "Atualizando..."

        lifecycleScope.launch {

            try {

                
                repository.sincronizarRegistros()

                
                val registros = repository.observarRegistros().first()

                atualizarGraficos(registros)

                Toast.makeText(
                    this@GraficosActivity,
                    "Resultados atualizados com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                
                
                try {

                    val registrosLocais =
                        repository.observarRegistros().first()

                    atualizarGraficos(registrosLocais)

                    Toast.makeText(
                        this@GraficosActivity,
                        "Não foi possível atualizar pela API. Exibindo dados locais.",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (localError: CancellationException) {

                    throw localError

                } catch (localError: Exception) {

                    Toast.makeText(
                        this@GraficosActivity,
                        "Erro ao carregar resultados: ${localError.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {

                carregando = false

                btnAtualizar.isEnabled = true
                btnAtualizar.text = "Atualizar Resultados"
            }
        }
    }

    
    private fun atualizarGraficos(registros: List<Registro>) {

        
        val totalRegistros = registros.size

        
        val totalItens = registros.sumOf {
            it.quantidade
        }

        
        val valorTotal = registros.sumOf {
            it.valor * it.quantidade
        }

        
        val valorMedio = if (registros.isNotEmpty()) {

            registros.map {
                it.valor
            }.average()

        } else {

            0.0
        }

        
        tvTotalRegistros.text =
            "Total de registros: $totalRegistros"

        tvTotalItens.text =
            "Total de itens: $totalItens"

        tvValorTotal.text = String.format(
            localeBR,
            "Valor total: R$ %.2f",
            valorTotal
        )

        tvValorMedio.text = String.format(
            localeBR,
            "Valor médio: R$ %.2f",
            valorMedio
        )

        
        val eletronicos = registros.count {

            it.categoria.trim().equals(
                "Eletrônicos",
                ignoreCase = true
            )
        }

        val perifericos = registros.count {

            it.categoria.trim().equals(
                "Periféricos",
                ignoreCase = true
            )
        }

        val moveis = registros.count {

            it.categoria.trim().equals(
                "Móveis",
                ignoreCase = true
            )
        }

        val outro = registros.count {
            it.categoria.trim().equals("Outro", ignoreCase = true)
        }

        
        
        tvEletronicos.text =
            "Eletrônicos: $eletronicos"

        tvPerifericos.text =
            "Periféricos: $perifericos"

        tvMoveis.text =
            "Móveis: $moveis"

        tvOutro.text =
            "Outro: $outro"

        
        
        val maiorQuantidade = maxOf(
            eletronicos,
            perifericos,
            moveis,
            outro,
            1
        )

        progressEletronicos.max = 100
        progressPerifericos.max = 100
        progressMoveis.max = 100

        progressEletronicos.progress =
            ((eletronicos.toLong() * 100) / maiorQuantidade).toInt()

        progressPerifericos.progress =
            ((perifericos.toLong() * 100) / maiorQuantidade).toInt()

        progressMoveis.progress =
            ((moveis.toLong() * 100) / maiorQuantidade).toInt()
    }
}

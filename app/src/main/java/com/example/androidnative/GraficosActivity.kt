package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class GraficosActivity : AppCompatActivity() {

    private lateinit var registroDao: RegistroDao

    private lateinit var tvTotalRegistros: TextView
    private lateinit var tvTotalItens: TextView
    private lateinit var tvValorTotal: TextView
    private lateinit var tvValorMedio: TextView

    private lateinit var tvEletronicos: TextView
    private lateinit var tvPerifericos: TextView
    private lateinit var tvMoveis: TextView
    private lateinit var tvOutrasCategorias: TextView

    private lateinit var progressEletronicos: ProgressBar
    private lateinit var progressPerifericos: ProgressBar
    private lateinit var progressMoveis: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_graficos)

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

        registroDao = AppDatabase
            .getDatabase(this)
            .registroDao()

        tvTotalRegistros =
            findViewById(R.id.tvTotalRegistros)

        tvTotalItens =
            findViewById(R.id.tvTotalItens)

        tvValorTotal =
            findViewById(R.id.tvValorTotal)

        tvValorMedio =
            findViewById(R.id.tvValorMedio)

        tvEletronicos =
            findViewById(R.id.tvEletronicos)

        tvPerifericos =
            findViewById(R.id.tvPerifericos)

        tvMoveis =
            findViewById(R.id.tvMoveis)

        tvOutrasCategorias =
            findViewById(R.id.tvOutrasCategorias)

        progressEletronicos =
            findViewById(R.id.progressEletronicos)

        progressPerifericos =
            findViewById(R.id.progressPerifericos)

        progressMoveis =
            findViewById(R.id.progressMoveis)

        val btnAtualizar =
            findViewById<Button>(R.id.btnAtualizar)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        btnAtualizar.setOnClickListener {
            carregarResultados()
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarResultados()
    }

    private fun carregarResultados() {

        lifecycleScope.launch {

            val registros =
                registroDao.listarSnapshot()

            val totalRegistros =
                registros.size

            val totalItens =
                registros.sumOf {
                    it.quantidade
                }

            val valorTotal =
                registros.sumOf {
                    it.valor * it.quantidade
                }

            val valorMedio =
                if (registros.isNotEmpty()) {
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

            tvValorTotal.text =
                String.format(
                    Locale("pt", "BR"),
                    "Valor total: R$ %.2f",
                    valorTotal
                )

            tvValorMedio.text =
                String.format(
                    Locale("pt", "BR"),
                    "Valor médio: R$ %.2f",
                    valorMedio
                )

            val eletronicos =
                registros.count {
                    it.categoria.equals(
                        "Eletrônicos",
                        ignoreCase = true
                    )
                }

            val perifericos =
                registros.count {
                    it.categoria.equals(
                        "Periféricos",
                        ignoreCase = true
                    )
                }

            val moveis =
                registros.count {
                    it.categoria.equals(
                        "Móveis",
                        ignoreCase = true
                    )
                }

            val outras =
                totalRegistros -
                        eletronicos -
                        perifericos -
                        moveis

            tvEletronicos.text =
                "Eletrônicos: $eletronicos"

            tvPerifericos.text =
                "Periféricos: $perifericos"

            tvMoveis.text =
                "Móveis: $moveis"

            tvOutrasCategorias.text =
                "Outras categorias: $outras"

            val maiorQuantidade =
                maxOf(
                    eletronicos,
                    perifericos,
                    moveis,
                    outras,
                    1
                )

            progressEletronicos.progress =
                (eletronicos * 100) / maiorQuantidade

            progressPerifericos.progress =
                (perifericos * 100) / maiorQuantidade

            progressMoveis.progress =
                (moveis * 100) / maiorQuantidade
        }
    }
}
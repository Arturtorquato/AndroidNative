package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class ProcessamentoActivity : AppCompatActivity() {

    private lateinit var registros: List<Registro>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_processamento)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        val btnCalcularTotal =
            findViewById<Button>(R.id.btnCalcularTotal)

        val btnCalcularMedia =
            findViewById<Button>(R.id.btnCalcularMedia)

        val btnMaiorValor =
            findViewById<Button>(R.id.btnMaiorValor)

        val btnOrdenar =
            findViewById<Button>(R.id.btnOrdenar)

        val btnProcessar =
            findViewById<Button>(R.id.btnProcessar)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        val tvResultado =
            findViewById<TextView>(R.id.tvResultado)

        val tvDetalhes =
            findViewById<TextView>(R.id.tvDetalhes)

        registros = listOf(
            Registro(
                "Notebook",
                "Notebook para desenvolvimento",
                "Eletrônicos",
                3500.00,
                5
            ),
            Registro(
                "Monitor",
                "Monitor Full HD",
                "Eletrônicos",
                1200.00,
                8
            ),
            Registro(
                "Teclado",
                "Teclado mecânico",
                "Periféricos",
                350.00,
                15
            ),
            Registro(
                "Mouse",
                "Mouse sem fio",
                "Periféricos",
                180.00,
                20
            ),
            Registro(
                "Cadeira",
                "Cadeira para escritório",
                "Móveis",
                950.00,
                7
            ),
            Registro(
                "Mesa",
                "Mesa para computador",
                "Móveis",
                700.00,
                4
            )
        )

        btnCalcularTotal.setOnClickListener {

            val total = registros.sumOf {
                it.valor * it.quantidade
            }

            tvResultado.text = "Valor total"
            tvDetalhes.text = String.format(
                Locale("pt", "BR"),
                "R$ %.2f",
                total
            )
        }

        btnCalcularMedia.setOnClickListener {

            val media = registros.map { it.valor }.average()

            tvResultado.text = "Valor médio"
            tvDetalhes.text = String.format(
                Locale("pt", "BR"),
                "R$ %.2f",
                media
            )
        }

        btnMaiorValor.setOnClickListener {

            val maior = registros.maxByOrNull { it.valor }

            if (maior != null) {
                tvResultado.text = "Maior valor"
                tvDetalhes.text = String.format(
                    Locale("pt", "BR"),
                    "%s - R$ %.2f",
                    maior.titulo,
                    maior.valor
                )
            }
        }

        btnOrdenar.setOnClickListener {

            val ordenados = registros.sortedByDescending {
                it.valor
            }

            tvResultado.text = "Registros ordenados"

            val resultado = ordenados.joinToString("\n") {
                String.format(
                    Locale("pt", "BR"),
                    "%s - R$ %.2f",
                    it.titulo,
                    it.valor
                )
            }

            tvDetalhes.text = resultado
        }

        btnProcessar.setOnClickListener {

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
                Locale("pt", "BR"),
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

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
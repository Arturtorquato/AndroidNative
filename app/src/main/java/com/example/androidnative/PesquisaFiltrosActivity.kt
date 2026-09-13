package com.example.androidnative

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PesquisaFiltrosActivity : AppCompatActivity() {

    private lateinit var registros: List<Registro>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_pesquisa_filtros)

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

        val edtPesquisa = findViewById<EditText>(R.id.edtPesquisa)
        val spinnerCategoria =
            findViewById<Spinner>(R.id.spinnerCategoria)

        val edtValorMinimo =
            findViewById<EditText>(R.id.edtValorMinimo)

        val edtValorMaximo =
            findViewById<EditText>(R.id.edtValorMaximo)

        val btnAplicarFiltros =
            findViewById<Button>(R.id.btnAplicarFiltros)

        val btnLimparFiltros =
            findViewById<Button>(R.id.btnLimparFiltros)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        val tvResultado =
            findViewById<TextView>(R.id.tvResultado)

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

        val categorias = listOf(
            "Todas",
            "Eletrônicos",
            "Periféricos",
            "Móveis"
        )

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategoria.adapter = spinnerAdapter

        btnAplicarFiltros.setOnClickListener {

            val textoPesquisa =
                edtPesquisa.text.toString().trim()

            val categoriaSelecionada =
                spinnerCategoria.selectedItem.toString()

            val valorMinimo =
                edtValorMinimo.text.toString()
                    .toDoubleOrNull()

            val valorMaximo =
                edtValorMaximo.text.toString()
                    .toDoubleOrNull()

            val resultados = registros.filter { registro ->

                val correspondeTexto =
                    textoPesquisa.isEmpty() ||
                            registro.titulo.contains(
                                textoPesquisa,
                                ignoreCase = true
                            )

                val correspondeCategoria =
                    categoriaSelecionada == "Todas" ||
                            registro.categoria == categoriaSelecionada

                val correspondeValorMinimo =
                    valorMinimo == null ||
                            registro.valor >= valorMinimo

                val correspondeValorMaximo =
                    valorMaximo == null ||
                            registro.valor <= valorMaximo

                correspondeTexto &&
                        correspondeCategoria &&
                        correspondeValorMinimo &&
                        correspondeValorMaximo
            }

            tvResultado.text =
                "Resultados encontrados: ${resultados.size}"
        }

        btnLimparFiltros.setOnClickListener {

            edtPesquisa.text.clear()
            edtValorMinimo.text.clear()
            edtValorMaximo.text.clear()

            spinnerCategoria.setSelection(0)

            tvResultado.text =
                "Resultados encontrados: ${registros.size}"
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        tvResultado.text =
            "Resultados encontrados: ${registros.size}"
    }
}
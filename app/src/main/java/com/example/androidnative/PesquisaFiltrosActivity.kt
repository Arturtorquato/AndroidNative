package com.example.androidnative

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class PesquisaFiltrosActivity : AppCompatActivity() {

    private lateinit var registroDao: RegistroDao
    private lateinit var adapter: RegistroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_pesquisa_filtros)

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

        val edtPesquisa =
            findViewById<EditText>(R.id.edtPesquisa)

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

        val recyclerResultados =
            findViewById<RecyclerView>(R.id.recyclerResultados)

        adapter = RegistroAdapter(emptyList()) { registro ->

            val intent = Intent(
                this,
                DetalhesRegistroActivity::class.java
            )

            intent.putExtra("id", registro.id)

            startActivity(intent)
        }

        recyclerResultados.layoutManager =
            LinearLayoutManager(this)

        recyclerResultados.adapter = adapter

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

            val texto = edtPesquisa.text
                .toString()
                .trim()

            val categoria =
                spinnerCategoria.selectedItem.toString()

            val valorMinimo =
                edtValorMinimo.text
                    .toString()
                    .trim()
                    .toDoubleOrNull()

            val valorMaximo =
                edtValorMaximo.text
                    .toString()
                    .trim()
                    .toDoubleOrNull()

            lifecycleScope.launch {

                val resultados = registroDao.pesquisar(
                    texto = texto,
                    categoria = categoria,
                    valorMinimo = valorMinimo,
                    valorMaximo = valorMaximo
                )

                adapter.atualizarLista(resultados)

                tvResultado.text =
                    "Resultados encontrados: ${resultados.size}"
            }
        }

        btnLimparFiltros.setOnClickListener {

            edtPesquisa.text.clear()
            edtValorMinimo.text.clear()
            edtValorMaximo.text.clear()

            spinnerCategoria.setSelection(0)

            lifecycleScope.launch {

                val resultados = registroDao.pesquisar(
                    texto = "",
                    categoria = "Todas",
                    valorMinimo = null,
                    valorMaximo = null
                )

                adapter.atualizarLista(resultados)

                tvResultado.text =
                    "Resultados encontrados: ${resultados.size}"
            }
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {

            val resultados = registroDao.pesquisar(
                texto = "",
                categoria = "Todas",
                valorMinimo = null,
                valorMaximo = null
            )

            adapter.atualizarLista(resultados)

            tvResultado.text =
                "Resultados encontrados: ${resultados.size}"
        }
    }
}
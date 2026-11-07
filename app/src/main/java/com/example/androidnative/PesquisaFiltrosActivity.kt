
package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
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

class PesquisaFiltrosActivity : AppCompatActivity() {

    companion object {
        private const val TAG_TCC = "TCC_DESEMPENHO"
    }

    private lateinit var repository: RegistroRepository
    private lateinit var adapter: RegistroAdapter

    private lateinit var edtPesquisa: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var edtValorMinimo: EditText
    private lateinit var edtValorMaximo: EditText
    private lateinit var tvResultado: TextView

    private var registros = emptyList<Registro>()

    
    private var filtroTexto = ""
    private var filtroCategoria = "Todas"
    private var filtroValorMinimo: Double? = null
    private var filtroValorMaximo: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_pesquisa_filtros)

        configurarInsets()

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        edtPesquisa = findViewById(R.id.edtPesquisa)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        edtValorMinimo = findViewById(R.id.edtValorMinimo)
        edtValorMaximo = findViewById(R.id.edtValorMaximo)
        tvResultado = findViewById(R.id.tvResultado)

        val btnAplicarFiltros =
            findViewById<Button>(R.id.btnAplicarFiltros)

        val btnLimparFiltros =
            findViewById<Button>(R.id.btnLimparFiltros)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        val recyclerResultados =
            findViewById<RecyclerView>(R.id.recyclerResultados)

        
        adapter = RegistroAdapter(emptyList()) { registro ->

            val intent = Intent(
                this@PesquisaFiltrosActivity,
                DetalhesRegistroActivity::class.java
            )

            intent.putExtra("id", registro.id)
            startActivity(intent)
        }

        recyclerResultados.layoutManager =
            LinearLayoutManager(this)

        recyclerResultados.adapter = adapter

        
        val categorias = resources
            .getStringArray(R.array.categorias_filtro)
            .toList()

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategoria.adapter = spinnerAdapter

        
        lifecycleScope.launch {

            repository.observarRegistros().collectLatest { lista ->

                registros = lista

                
                
                atualizarResultados()
            }
        }

        btnAplicarFiltros.setOnClickListener {
            aplicarFiltros()
        }

        btnLimparFiltros.setOnClickListener {
            limparFiltros()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    
    private fun configurarInsets() {

        val root = findViewById<android.view.View>(R.id.main)

        val paddingEsquerdo = root.paddingLeft
        val paddingSuperior = root.paddingTop
        val paddingDireito = root.paddingRight
        val paddingInferior = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                paddingEsquerdo + systemBars.left,
                paddingSuperior + systemBars.top,
                paddingDireito + systemBars.right,
                paddingInferior + systemBars.bottom
            )

            insets
        }
    }

    
    private fun aplicarFiltros() {

        val texto = edtPesquisa.text.toString().trim()

        val categoria = spinnerCategoria.selectedItem.toString()

        val minimoTexto = edtValorMinimo.text
            .toString()
            .trim()
            .replace(",", ".")

        val maximoTexto = edtValorMaximo.text
            .toString()
            .trim()
            .replace(",", ".")

        val minimo = if (minimoTexto.isEmpty()) {
            null
        } else {
            minimoTexto.toDoubleOrNull()
        }

        val maximo = if (maximoTexto.isEmpty()) {
            null
        } else {
            maximoTexto.toDoubleOrNull()
        }

        if (
            minimoTexto.isNotEmpty() &&
            (minimo == null || !minimo.isFinite() || minimo < 0.0)
        ) {

            edtValorMinimo.error = "Digite um valor mínimo válido"
            edtValorMinimo.requestFocus()

            return
        }

        if (
            maximoTexto.isNotEmpty() &&
            (maximo == null || !maximo.isFinite() || maximo < 0.0)
        ) {

            edtValorMaximo.error = "Digite um valor máximo válido"
            edtValorMaximo.requestFocus()

            return
        }

        if (
            minimo != null &&
            maximo != null &&
            minimo > maximo
        ) {

            edtValorMaximo.error =
                "O valor máximo deve ser maior ou igual ao mínimo"

            edtValorMaximo.requestFocus()

            return
        }

        filtroTexto = texto
        filtroCategoria = categoria
        filtroValorMinimo = minimo
        filtroValorMaximo = maximo

        
        atualizarResultados(medirDesempenho = true)
    }

    
    private fun limparFiltros() {

        edtPesquisa.text.clear()
        edtValorMinimo.text.clear()
        edtValorMaximo.text.clear()

        spinnerCategoria.setSelection(0)

        filtroTexto = ""
        filtroCategoria = "Todas"
        filtroValorMinimo = null
        filtroValorMaximo = null

        atualizarResultados()
    }

    
    private fun atualizarResultados(
        medirDesempenho: Boolean = false
    ) {

        
        val inicioNanos = if (medirDesempenho) {
            SystemClock.elapsedRealtimeNanos()
        } else {
            0L
        }

        val resultados = registros.filter { registro ->

            
            val correspondeTexto =
                filtroTexto.isEmpty() ||

                        registro.titulo.contains(
                            filtroTexto,
                            ignoreCase = true
                        ) ||

                        registro.descricao.contains(
                            filtroTexto,
                            ignoreCase = true
                        ) ||

                        registro.categoria.contains(
                            filtroTexto,
                            ignoreCase = true
                        )

            
            val correspondeCategoria =
                filtroCategoria == "Todas" ||
                        registro.categoria == filtroCategoria

            
            val correspondeMinimo =
                filtroValorMinimo == null ||
                        registro.valor >= filtroValorMinimo!!

            
            val correspondeMaximo =
                filtroValorMaximo == null ||
                        registro.valor <= filtroValorMaximo!!

            correspondeTexto &&
                    correspondeCategoria &&
                    correspondeMinimo &&
                    correspondeMaximo
        }

        adapter.atualizarLista(resultados)

        tvResultado.text =
            "Resultados encontrados: ${resultados.size}"

        
        if (medirDesempenho) {

            val fimNanos = SystemClock.elapsedRealtimeNanos()

            val tempoMs =
                (fimNanos - inicioNanos) / 1_000_000.0

            val tipoOperacao = if (
                filtroTexto.isNotEmpty() &&
                filtroCategoria == "Todas" &&
                filtroValorMinimo == null &&
                filtroValorMaximo == null
            ) {
                "PESQUISA"
            } else {
                "FILTROS"
            }

            Log.i(
                TAG_TCC,
                "operacao=$tipoOperacao | " +
                        "totalRegistros=${registros.size} | " +
                        "resultados=${resultados.size} | " +
                        "tempoMs=$tempoMs | " +
                        "texto='$filtroTexto' | " +
                        "categoria='$filtroCategoria'"
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
                    this@PesquisaFiltrosActivity,
                    "Não foi possível atualizar pela API. Exibindo dados locais.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

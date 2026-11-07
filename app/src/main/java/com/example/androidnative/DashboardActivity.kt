
package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.example.androidnative.repository.RegistroRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    private lateinit var tvTotal: TextView
    private lateinit var tvItens: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvBoasVindas: TextView

    private var sincronizando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        configurarInsets()

        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        tvTotal = findViewById(R.id.tvTotal)
        tvItens = findViewById(R.id.tvItens)
        tvStatus = findViewById(R.id.tvStatus)
        tvBoasVindas = findViewById(R.id.tvBoasVindas)

        
        val nomeUsuario = intent.getStringExtra("usuarioNome")

        tvBoasVindas.text = if (!nomeUsuario.isNullOrBlank()) {
            "Olá, $nomeUsuario!"
        } else {
            "Olá, usuário!"
        }

        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnPesquisar = findViewById<Button>(R.id.btnPesquisar)
        val btnProcessamento = findViewById<Button>(R.id.btnProcessamento)
        val btnGraficos = findViewById<Button>(R.id.btnGraficos)
        val btnSair = findViewById<Button>(R.id.btnSair)

        
        lifecycleScope.launch {

            repository.observarRegistros().collectLatest { registros ->

                tvTotal.text = registros.size.toString()

                tvItens.text = registros.sumOf {
                    it.quantidade
                }.toString()
            }
        }

        
        btnLista.setOnClickListener {

            startActivity(
                Intent(this, ListaRegistrosActivity::class.java)
            )
        }

        
        btnCadastrar.setOnClickListener {

            startActivity(
                Intent(this, RegistroActivity::class.java)
            )
        }

        
        btnPesquisar.setOnClickListener {

            startActivity(
                Intent(this, PesquisaFiltrosActivity::class.java)
            )
        }

        
        btnProcessamento.setOnClickListener {

            startActivity(
                Intent(this, ProcessamentoActivity::class.java)
            )
        }

        
        btnGraficos.setOnClickListener {

            startActivity(
                Intent(this, GraficosActivity::class.java)
            )
        }

        
        btnSair.setOnClickListener {

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
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

        sincronizarDados()
    }

    private fun sincronizarDados() {

        if (sincronizando) return

        sincronizando = true

        tvStatus.text = "Sincronizando dados com a API..."

        lifecycleScope.launch {

            try {

                repository.sincronizarRegistros()

                tvStatus.text = "Dados atualizados com o servidor"

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                tvStatus.text =
                    "API indisponível - exibindo dados locais"

                Toast.makeText(
                    this@DashboardActivity,
                    "Não foi possível sincronizar. Exibindo dados locais.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                sincronizando = false
            }
        }
    }
}

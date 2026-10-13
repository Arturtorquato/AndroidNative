package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

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

        val btnLista =
            findViewById<Button>(R.id.btnLista)

        val btnCadastrar =
            findViewById<Button>(R.id.btnCadastrar)

        val btnPesquisar =
            findViewById<Button>(R.id.btnPesquisar)

        val btnSair =
            findViewById<Button>(R.id.btnSair)

        val btnProcessamento =
            findViewById<Button>(R.id.btnProcessamento)

        val btnGraficos =
            findViewById<Button>(R.id.btnGraficos)

        val tvTotal =
            findViewById<TextView>(R.id.tvTotal)

        val tvItens =
            findViewById<TextView>(R.id.tvItens)

        val registroDao = AppDatabase
            .getDatabase(this)
            .registroDao()

        lifecycleScope.launch {

            registroDao.listar().collect { registros ->

                tvTotal.text =
                    registros.size.toString()

                val quantidadeItens = registros.sumOf {
                    it.quantidade
                }

                tvItens.text =
                    quantidadeItens.toString()
            }
        }

        btnLista.setOnClickListener {

            val intent = Intent(
                this,
                ListaRegistrosActivity::class.java
            )

            startActivity(intent)
        }

        btnCadastrar.setOnClickListener {

            val intent = Intent(
                this,
                RegistroActivity::class.java
            )

            startActivity(intent)
        }

        btnPesquisar.setOnClickListener {

            val intent = Intent(
                this,
                PesquisaFiltrosActivity::class.java
            )

            startActivity(intent)
        }

        btnProcessamento.setOnClickListener {

            val intent = Intent(
                this,
                ProcessamentoActivity::class.java
            )

            startActivity(intent)
        }

        btnGraficos.setOnClickListener {

            val intent = Intent(
                this,
                GraficosActivity::class.java
            )

            startActivity(intent)
        }

        btnSair.setOnClickListener {
            finish()
        }

        val btnTestarApi = findViewById<Button>(R.id.btnTestarApi)

        btnTestarApi.setOnClickListener {
            val intent = Intent(this, ApiTestActivity::class.java)
            startActivity(intent)
        }
    }
}
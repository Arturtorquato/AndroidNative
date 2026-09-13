package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class DetalhesRegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_registro)

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

        val tvValorTitulo = findViewById<TextView>(R.id.tvValorTitulo)
        val tvValorDescricao = findViewById<TextView>(R.id.tvValorDescricao)
        val tvValorCategoria = findViewById<TextView>(R.id.tvValorCategoria)
        val tvValor = findViewById<TextView>(R.id.tvValor)
        val tvValorQuantidade = findViewById<TextView>(R.id.tvValorQuantidade)

        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        val titulo = intent.getStringExtra("titulo") ?: "Sem título"
        val descricao = intent.getStringExtra("descricao") ?: "Sem descrição"
        val categoria = intent.getStringExtra("categoria") ?: "Sem categoria"
        val valor = intent.getDoubleExtra("valor", 0.0)
        val quantidade = intent.getIntExtra("quantidade", 0)

        tvValorTitulo.text = titulo
        tvValorDescricao.text = descricao
        tvValorCategoria.text = categoria

        tvValor.text = String.format(
            Locale("pt", "BR"),
            "R$ %.2f",
            valor
        )

        tvValorQuantidade.text = quantidade.toString()

        btnEditar.setOnClickListener {

            val intent = Intent(
                this,
                EditarRegistroActivity::class.java
            )

            intent.putExtra("titulo", titulo)
            intent.putExtra("descricao", descricao)
            intent.putExtra("categoria", categoria)
            intent.putExtra("valor", valor)
            intent.putExtra("quantidade", quantidade)

            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            Toast.makeText(
                this,
                "Excluir registro",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
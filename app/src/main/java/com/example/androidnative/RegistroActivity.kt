package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

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

        val edtTitulo = findViewById<EditText>(R.id.edtTitulo)
        val edtDescricao = findViewById<EditText>(R.id.edtDescricao)
        val edtCategoria = findViewById<EditText>(R.id.edtCategoria)
        val edtValor = findViewById<EditText>(R.id.edtValor)
        val edtQuantidade = findViewById<EditText>(R.id.edtQuantidade)

        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnSalvar.setOnClickListener {

            val titulo = edtTitulo.text.toString().trim()
            val descricao = edtDescricao.text.toString().trim()
            val categoria = edtCategoria.text.toString().trim()
            val valor = edtValor.text.toString().trim()
            val quantidade = edtQuantidade.text.toString().trim()

            if (titulo.isEmpty()) {
                edtTitulo.error = "Digite o título"
                return@setOnClickListener
            }

            if (descricao.isEmpty()) {
                edtDescricao.error = "Digite a descrição"
                return@setOnClickListener
            }

            if (categoria.isEmpty()) {
                edtCategoria.error = "Digite a categoria"
                return@setOnClickListener
            }

            if (valor.isEmpty()) {
                edtValor.error = "Digite o valor"
                return@setOnClickListener
            }

            if (quantidade.isEmpty()) {
                edtQuantidade.error = "Digite a quantidade"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Registro cadastrado com sucesso",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
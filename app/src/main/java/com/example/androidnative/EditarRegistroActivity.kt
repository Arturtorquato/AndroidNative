package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarRegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_registro)

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

        val btnSalvarAlteracoes =
            findViewById<Button>(R.id.btnSalvarAlteracoes)

        val btnCancelar =
            findViewById<Button>(R.id.btnCancelar)

        // Recebe os dados enviados pela tela de detalhes.
        val titulo = intent.getStringExtra("titulo") ?: ""
        val descricao = intent.getStringExtra("descricao") ?: ""
        val categoria = intent.getStringExtra("categoria") ?: ""
        val valor = intent.getDoubleExtra("valor", 0.0)
        val quantidade = intent.getIntExtra("quantidade", 0)

        // Preenche os campos com os dados atuais.
        edtTitulo.setText(titulo)
        edtDescricao.setText(descricao)
        edtCategoria.setText(categoria)
        edtValor.setText(valor.toString())
        edtQuantidade.setText(quantidade.toString())

        btnSalvarAlteracoes.setOnClickListener {

            val novoTitulo = edtTitulo.text.toString().trim()
            val novaDescricao = edtDescricao.text.toString().trim()
            val novaCategoria = edtCategoria.text.toString().trim()
            val novoValor = edtValor.text.toString().trim()
            val novaQuantidade = edtQuantidade.text.toString().trim()

            if (novoTitulo.isEmpty()) {
                edtTitulo.error = "Digite o título"
                return@setOnClickListener
            }

            if (novaDescricao.isEmpty()) {
                edtDescricao.error = "Digite a descrição"
                return@setOnClickListener
            }

            if (novaCategoria.isEmpty()) {
                edtCategoria.error = "Digite a categoria"
                return@setOnClickListener
            }

            if (novoValor.isEmpty()) {
                edtValor.error = "Digite o valor"
                return@setOnClickListener
            }

            if (novaQuantidade.isEmpty()) {
                edtQuantidade.error = "Digite a quantidade"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Alterações salvas com sucesso",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
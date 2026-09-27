package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditarRegistroActivity : AppCompatActivity() {

    private lateinit var registroDao: RegistroDao

    private var registroId: Int = -1

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

        registroDao = AppDatabase
            .getDatabase(this)
            .registroDao()

        registroId = intent.getIntExtra("id", -1)

        val edtTitulo =
            findViewById<EditText>(R.id.edtTitulo)

        val edtDescricao =
            findViewById<EditText>(R.id.edtDescricao)

        val edtCategoria =
            findViewById<EditText>(R.id.edtCategoria)

        val edtValor =
            findViewById<EditText>(R.id.edtValor)

        val edtQuantidade =
            findViewById<EditText>(R.id.edtQuantidade)

        val btnSalvarAlteracoes =
            findViewById<Button>(R.id.btnSalvarAlteracoes)

        val btnCancelar =
            findViewById<Button>(R.id.btnCancelar)

        if (registroId == -1) {

            Toast.makeText(
                this,
                "Registro inválido",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        lifecycleScope.launch {

            val registro =
                registroDao.buscarPorId(registroId)

            if (registro == null) {

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Registro não encontrado",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            edtTitulo.setText(registro.titulo)
            edtDescricao.setText(registro.descricao)
            edtCategoria.setText(registro.categoria)
            edtValor.setText(registro.valor.toString())
            edtQuantidade.setText(registro.quantidade.toString())
        }

        btnSalvarAlteracoes.setOnClickListener {

            val titulo =
                edtTitulo.text.toString().trim()

            val descricao =
                edtDescricao.text.toString().trim()

            val categoria =
                edtCategoria.text.toString().trim()

            val valorTexto =
                edtValor.text.toString().trim()

            val quantidadeTexto =
                edtQuantidade.text.toString().trim()

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

            if (valorTexto.isEmpty()) {
                edtValor.error = "Digite o valor"
                return@setOnClickListener
            }

            if (quantidadeTexto.isEmpty()) {
                edtQuantidade.error = "Digite a quantidade"
                return@setOnClickListener
            }

            val valor = valorTexto.toDoubleOrNull()

            if (valor == null) {
                edtValor.error = "Digite um valor válido"
                return@setOnClickListener
            }

            val quantidade =
                quantidadeTexto.toIntOrNull()

            if (quantidade == null) {
                edtQuantidade.error =
                    "Digite uma quantidade válida"

                return@setOnClickListener
            }

            val registroAtualizado = Registro(
                id = registroId,
                titulo = titulo,
                descricao = descricao,
                categoria = categoria,
                valor = valor,
                quantidade = quantidade
            )

            lifecycleScope.launch {

                registroDao.atualizar(
                    registroAtualizado
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Alterações salvas com sucesso",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidnative.repository.RegistroRepository
import kotlinx.coroutines.launch
import java.io.IOException

class RegistroActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

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

        
        repository = RegistroRepository(
            AppDatabase.getDatabase(this)
        )

        val edtTitulo =
            findViewById<EditText>(R.id.edtTitulo)

        val edtDescricao =
            findViewById<EditText>(R.id.edtDescricao)

        val spinnerCategoria =
            findViewById<Spinner>(R.id.spinnerCategoria)

        val edtValor =
            findViewById<EditText>(R.id.edtValor)

        val edtQuantidade =
            findViewById<EditText>(R.id.edtQuantidade)

        val btnSalvar =
            findViewById<Button>(R.id.btnSalvar)

        val btnCancelar =
            findViewById<Button>(R.id.btnCancelar)

        btnSalvar.setOnClickListener {

            val titulo = edtTitulo.text.toString().trim()
            val descricao = edtDescricao.text.toString().trim()
            val categoria = spinnerCategoria.selectedItem.toString()
            val valorTexto = edtValor.text.toString().trim()
            val quantidadeTexto = edtQuantidade.text.toString().trim()

            

            if (titulo.isEmpty()) {
                edtTitulo.error = "Digite o título"
                return@setOnClickListener
            }

            if (descricao.isEmpty()) {
                edtDescricao.error = "Digite a descrição"
                return@setOnClickListener
            }

            val valor = valorTexto.toDoubleOrNull()

            if (valor == null || !valor.isFinite() || valor < 0) {
                edtValor.error = "Digite um valor válido"
                return@setOnClickListener
            }

            val quantidade = quantidadeTexto.toIntOrNull()

            if (quantidade == null || quantidade < 0) {
                edtQuantidade.error = "Digite uma quantidade válida"
                return@setOnClickListener
            }

            
            btnSalvar.isEnabled = false

            lifecycleScope.launch {

                try {

                    val registro = repository.cadastrarRegistro(
                        titulo = titulo,
                        descricao = descricao,
                        categoria = categoria,
                        valor = valor,
                        quantidade = quantidade
                    )

                    Toast.makeText(
                        this@RegistroActivity,
                        "Registro ${registro.id} cadastrado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                } catch (e: IOException) {

                    Toast.makeText(
                        this@RegistroActivity,
                        "Falha na operação. Verifique a API e sincronize antes de tentar novamente.",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@RegistroActivity,
                        "Erro ao cadastrar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {

                    btnSalvar.isEnabled = true

                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}

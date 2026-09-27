package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

class DetalhesRegistroActivity : AppCompatActivity() {

    private lateinit var registroDao: RegistroDao

    private var registroId: Int = -1

    private lateinit var tvValorTitulo: TextView
    private lateinit var tvValorDescricao: TextView
    private lateinit var tvValorCategoria: TextView
    private lateinit var tvValor: TextView
    private lateinit var tvValorQuantidade: TextView

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

        registroDao = AppDatabase
            .getDatabase(this)
            .registroDao()

        registroId = intent.getIntExtra("id", -1)

        tvValorTitulo = findViewById(R.id.tvValorTitulo)
        tvValorDescricao = findViewById(R.id.tvValorDescricao)
        tvValorCategoria = findViewById(R.id.tvValorCategoria)
        tvValor = findViewById(R.id.tvValor)
        tvValorQuantidade = findViewById(R.id.tvValorQuantidade)

        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnEditar.setOnClickListener {

            if (registroId == -1) {
                Toast.makeText(
                    this,
                    "Registro inválido",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(
                this,
                EditarRegistroActivity::class.java
            )

            intent.putExtra("id", registroId)

            startActivity(intent)
        }

        btnExcluir.setOnClickListener {
            confirmarExclusao()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (registroId != -1) {
            carregarRegistro()
        }
    }

    private fun carregarRegistro() {

        lifecycleScope.launch {

            val registro = registroDao.buscarPorId(registroId)

            if (registro == null) {

                Toast.makeText(
                    this@DetalhesRegistroActivity,
                    "Registro não encontrado",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            tvValorTitulo.text = registro.titulo
            tvValorDescricao.text = registro.descricao
            tvValorCategoria.text = registro.categoria

            tvValor.text = String.format(
                Locale("pt", "BR"),
                "R$ %.2f",
                registro.valor
            )

            tvValorQuantidade.text =
                registro.quantidade.toString()
        }
    }

    private fun confirmarExclusao() {

        AlertDialog.Builder(this)
            .setTitle("Excluir registro")
            .setMessage("Deseja realmente excluir este registro?")
            .setPositiveButton("Excluir") { _, _ ->

                lifecycleScope.launch {

                    val registro = registroDao.buscarPorId(registroId)

                    if (registro != null) {

                        registroDao.excluir(registro)

                        Toast.makeText(
                            this@DetalhesRegistroActivity,
                            "Registro excluído com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
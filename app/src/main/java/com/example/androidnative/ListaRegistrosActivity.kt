package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListaRegistrosActivity : AppCompatActivity() {

    private lateinit var adapter: RegistroAdapter
    private lateinit var registroDao: RegistroDao

    private var registros = emptyList<Registro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_registros)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.recyclerRegistros)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                0
            )

            insets
        }

        val edtPesquisa =
            findViewById<EditText>(R.id.edtPesquisa)

        val btnPesquisar =
            findViewById<Button>(R.id.btnPesquisar)

        val btnVoltar =
            findViewById<Button>(R.id.btnVoltar)

        val recyclerRegistros =
            findViewById<RecyclerView>(R.id.recyclerRegistros)

        registroDao = AppDatabase
            .getDatabase(this)
            .registroDao()

        adapter = RegistroAdapter(emptyList()) { registro ->

            val intent = Intent(
                this,
                DetalhesRegistroActivity::class.java
            )

            intent.putExtra("id", registro.id)
            intent.putExtra("titulo", registro.titulo)
            intent.putExtra("descricao", registro.descricao)
            intent.putExtra("categoria", registro.categoria)
            intent.putExtra("valor", registro.valor)
            intent.putExtra("quantidade", registro.quantidade)

            startActivity(intent)
        }

        recyclerRegistros.layoutManager =
            LinearLayoutManager(this)

        recyclerRegistros.adapter = adapter

        lifecycleScope.launch {

            registroDao.listar().collect { lista ->

                registros = lista

                adapter.atualizarLista(lista)
            }
        }

        btnPesquisar.setOnClickListener {

            val texto =
                edtPesquisa.text.toString().trim()

            if (texto.isEmpty()) {

                adapter.atualizarLista(registros)

                return@setOnClickListener
            }

            val filtrados = registros.filter { registro ->

                registro.titulo.contains(
                    texto,
                    ignoreCase = true
                ) ||
                        registro.categoria.contains(
                            texto,
                            ignoreCase = true
                        ) ||
                        registro.descricao.contains(
                            texto,
                            ignoreCase = true
                        )
            }

            adapter.atualizarLista(filtrados)
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
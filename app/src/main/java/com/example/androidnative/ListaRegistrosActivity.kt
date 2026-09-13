package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaRegistrosActivity : AppCompatActivity() {

    private lateinit var adapter: RegistroAdapter
    private lateinit var registros: List<Registro>

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

        val edtPesquisa = findViewById<EditText>(R.id.edtPesquisa)
        val btnPesquisar = findViewById<Button>(R.id.btnPesquisar)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        val recyclerRegistros =
            findViewById<RecyclerView>(R.id.recyclerRegistros)

        registros = listOf(
            Registro(
                "Notebook",
                "Notebook para desenvolvimento",
                "Eletrônicos",
                3500.00,
                5
            ),
            Registro(
                "Monitor",
                "Monitor Full HD",
                "Eletrônicos",
                1200.00,
                8
            ),
            Registro(
                "Teclado",
                "Teclado mecânico",
                "Periféricos",
                350.00,
                15
            ),
            Registro(
                "Mouse",
                "Mouse sem fio",
                "Periféricos",
                180.00,
                20
            ),
            Registro(
                "Cadeira",
                "Cadeira para escritório",
                "Móveis",
                950.00,
                7
            ),
            Registro(
                "Mesa",
                "Mesa para computador",
                "Móveis",
                700.00,
                4
            )
        )

        adapter = RegistroAdapter(registros) { registro ->

            val intent = Intent(
                this,
                DetalhesRegistroActivity::class.java
            )

            intent.putExtra("titulo", registro.titulo)
            intent.putExtra("descricao", registro.descricao)
            intent.putExtra("categoria", registro.categoria)
            intent.putExtra("valor", registro.valor)
            intent.putExtra("quantidade", registro.quantidade)

            startActivity(intent)
        }

        recyclerRegistros.layoutManager = LinearLayoutManager(this)
        recyclerRegistros.adapter = adapter

        btnPesquisar.setOnClickListener {

            val texto = edtPesquisa.text.toString().trim()

            if (texto.isEmpty()) {
                adapter.atualizarLista(registros)
                return@setOnClickListener
            }

            val filtrados = registros.filter { registro ->
                registro.titulo.contains(texto, ignoreCase = true) ||
                        registro.categoria.contains(texto, ignoreCase = true)
            }

            adapter.atualizarLista(filtrados)
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
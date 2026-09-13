package com.example.androidnative
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

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

        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnPesquisar = findViewById<Button>(R.id.btnPesquisar)
        val btnSair = findViewById<Button>(R.id.btnSair)

        btnLista.setOnClickListener {
            val intent = Intent(this, ListaRegistrosActivity::class.java)
            startActivity(intent)
        }

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnPesquisar.setOnClickListener {
            val intent = Intent(
                this,
                PesquisaFiltrosActivity::class.java
            )

            startActivity(intent)
        }

        btnSair.setOnClickListener {
            finish()
        }
    }
}
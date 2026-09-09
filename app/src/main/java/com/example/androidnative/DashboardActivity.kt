package com.example.androidnative
import android.content.Intent
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
            Toast.makeText(
                this,
                "Lista de registros",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnPesquisar.setOnClickListener {
            Toast.makeText(
                this,
                "Pesquisar registros",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnSair.setOnClickListener {
            finish()
        }
    }
}
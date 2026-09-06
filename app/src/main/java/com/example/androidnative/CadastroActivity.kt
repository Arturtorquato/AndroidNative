package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

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

        val edtNome = findViewById<EditText>(R.id.edtNome)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val edtConfirmarSenha =
            findViewById<EditText>(R.id.edtConfirmarSenha)

        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnSalvar.setOnClickListener {

            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString()
            val confirmarSenha = edtConfirmarSenha.text.toString()

            if (nome.isEmpty()) {
                edtNome.error = "Digite o nome"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                edtEmail.error = "Digite o e-mail"
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                edtSenha.error = "Digite a senha"
                return@setOnClickListener
            }

            if (confirmarSenha.isEmpty()) {
                edtConfirmarSenha.error = "Confirme a senha"
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                edtConfirmarSenha.error = "As senhas não coincidem"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Usuário cadastrado com sucesso",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
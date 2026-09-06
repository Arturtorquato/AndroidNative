package com.example.androidnative

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCadastro = findViewById<Button>(R.id.btnCadastro)

        btnLogin.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString()

            if (email.isEmpty()) {
                edtEmail.error = "Digite o e-mail"
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                edtSenha.error = "Digite a senha"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Login realizado com sucesso",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}
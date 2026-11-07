package com.example.androidnative

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope

import com.example.androidnative.api.LoginRequest
import com.example.androidnative.api.UsuarioApiClient

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnCadastro: Button

    private var realizandoLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        configurarInsets()

        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)

        btnLogin = findViewById(R.id.btnLogin)
        btnCadastro = findViewById(R.id.btnCadastro)

        btnLogin.setOnClickListener {
            realizarLogin()
        }

        btnCadastro.setOnClickListener {

            val intent = Intent(
                this,
                CadastroActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun configurarInsets() {

        val root = findViewById<View>(R.id.main)

        val esquerda = root.paddingLeft
        val superior = root.paddingTop
        val direita = root.paddingRight
        val inferior = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                esquerda + systemBars.left,
                superior + systemBars.top,
                direita + systemBars.right,
                inferior + systemBars.bottom
            )

            insets
        }
    }

    private fun realizarLogin() {

        if (realizandoLogin) return

        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        if (email.isEmpty()) {

            edtEmail.error = "Digite o e-mail"
            edtEmail.requestFocus()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            edtEmail.error = "Digite um e-mail válido"
            edtEmail.requestFocus()

            return
        }

        if (senha.isEmpty()) {

            edtSenha.error = "Digite a senha"
            edtSenha.requestFocus()

            return
        }

        realizandoLogin = true

        btnLogin.isEnabled = false
        btnCadastro.isEnabled = false
        btnLogin.text = "Entrando..."

        lifecycleScope.launch {

            try {

                val request = LoginRequest(
                    email = email,
                    senha = senha
                )

                val resposta = UsuarioApiClient.usuarioApi.login(
                    request
                )

                if (resposta.isSuccessful) {

                    val usuario = resposta.body()

                    if (usuario == null) {

                        Toast.makeText(
                            this@MainActivity,
                            "Resposta inválida do servidor",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    Toast.makeText(
                        this@MainActivity,
                        "Bem-vindo, ${usuario.nome}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this@MainActivity,
                        DashboardActivity::class.java
                    )

                    
                    intent.putExtra("usuarioId", usuario.id)
                    intent.putExtra("usuarioNome", usuario.nome)
                    intent.putExtra("usuarioEmail", usuario.email)

                    startActivity(intent)

                    finish()

                } else {

                    val mensagem = when (resposta.code()) {

                        400 -> "Informe o e-mail e a senha corretamente."

                        401 -> "E-mail ou senha incorretos."

                        404 -> "Endpoint de login não encontrado."

                        500 -> "Erro interno no servidor."

                        else -> "Erro HTTP: ${resposta.code()}"
                    }

                    Toast.makeText(
                        this@MainActivity,
                        mensagem,
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainActivity,
                    "Não foi possível conectar à API. Verifique o servidor e a conexão.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                realizandoLogin = false

                btnLogin.isEnabled = true
                btnCadastro.isEnabled = true
                btnLogin.text = "Entrar"
            }
        }
    }
}
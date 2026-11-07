package com.example.androidnative

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

import com.example.androidnative.api.UsuarioApiClient
import com.example.androidnative.api.UsuarioCadastroRequest

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtConfirmarSenha: EditText

    private lateinit var btnSalvar: Button
    private lateinit var btnVoltar: Button

    private var cadastrando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        configurarInsets()

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)

        edtConfirmarSenha = findViewById(
            R.id.edtConfirmarSenha
        )

        btnSalvar = findViewById(R.id.btnSalvar)
        btnVoltar = findViewById(R.id.btnVoltar)

        btnSalvar.setOnClickListener {
            cadastrarUsuario()
        }

        btnVoltar.setOnClickListener {
            finish()
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

    private fun cadastrarUsuario() {

        if (cadastrando) return

        val nome = edtNome.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        val confirmarSenha = edtConfirmarSenha.text.toString()

        
        if (nome.isEmpty()) {

            edtNome.error = "Digite o nome"
            edtNome.requestFocus()

            return
        }

        
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

        if (senha.length < 8) {

            edtSenha.error = "A senha deve possuir pelo menos 8 caracteres"
            edtSenha.requestFocus()

            return
        }

        
        if (confirmarSenha.isEmpty()) {

            edtConfirmarSenha.error = "Confirme a senha"
            edtConfirmarSenha.requestFocus()

            return
        }

        if (senha != confirmarSenha) {

            edtConfirmarSenha.error = "As senhas não coincidem"
            edtConfirmarSenha.requestFocus()

            return
        }

        cadastrando = true

        btnSalvar.isEnabled = false
        btnVoltar.isEnabled = false
        btnSalvar.text = "Cadastrando..."

        lifecycleScope.launch {

            try {

                val request = UsuarioCadastroRequest(
                    nome = nome,
                    email = email,
                    senha = senha
                )

                val resposta = UsuarioApiClient.usuarioApi.cadastrar(
                    request
                )

                if (resposta.isSuccessful) {

                    val usuario = resposta.body()

                    if (usuario == null) {

                        Toast.makeText(
                            this@CadastroActivity,
                            "Resposta inválida do servidor",
                            Toast.LENGTH_LONG
                        ).show()

                        return@launch
                    }

                    Toast.makeText(
                        this@CadastroActivity,
                        "Usuário ${usuario.nome} cadastrado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()

                    
                    finish()

                } else {

                    val mensagem = when (resposta.code()) {

                        400 -> "Verifique os dados informados."

                        409 -> "Este e-mail já está cadastrado."

                        404 -> "Endpoint de cadastro não encontrado."

                        500 -> "Erro interno no servidor."

                        else -> "Erro HTTP: ${resposta.code()}"
                    }

                    Toast.makeText(
                        this@CadastroActivity,
                        mensagem,
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                Toast.makeText(
                    this@CadastroActivity,
                    "Não foi possível conectar à API. Verifique o servidor antes de tentar novamente.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                cadastrando = false

                btnSalvar.isEnabled = true
                btnVoltar.isEnabled = true
                btnSalvar.text = "Cadastrar"
            }
        }
    }
}
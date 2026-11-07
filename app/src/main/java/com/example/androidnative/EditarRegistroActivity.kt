package com.example.androidnative

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
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

class EditarRegistroActivity : AppCompatActivity() {

    private lateinit var repository: RegistroRepository

    private var registroId: Long = -1L
    private var carregando = false
    private var salvando = false
    private var dadosCarregados = false

    private lateinit var edtTitulo: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var edtValor: EditText
    private lateinit var edtQuantidade: EditText

    private lateinit var btnSalvarAlteracoes: Button
    private lateinit var btnCancelar: Button

    companion object {
        private const val TAG_DESEMPENHO = "TCC_DESEMPENHO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_registro)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
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

        registroId = intent.getLongExtra("id", -1L)

        edtTitulo = findViewById(R.id.edtTitulo)
        edtDescricao = findViewById(R.id.edtDescricao)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        edtValor = findViewById(R.id.edtValor)
        edtQuantidade = findViewById(R.id.edtQuantidade)

        btnSalvarAlteracoes = findViewById(
            R.id.btnSalvarAlteracoes
        )

        btnCancelar = findViewById(R.id.btnCancelar)

        if (registroId <= 0L) {
            Toast.makeText(
                this,
                "Registro inválido",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        carregarRegistro()

        btnSalvarAlteracoes.setOnClickListener {
            salvarAlteracoes()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun carregarRegistro() {
        if (carregando) return

        carregando = true
        dadosCarregados = false
        btnSalvarAlteracoes.isEnabled = false

        lifecycleScope.launch {
            val inicioTotal = SystemClock.elapsedRealtimeNanos()

            try {
                val inicioConsulta = SystemClock.elapsedRealtimeNanos()

                val registro = repository.buscarRegistro(registroId)

                val consultaMs =
                    (SystemClock.elapsedRealtimeNanos() - inicioConsulta) /
                            1_000_000.0

                val inicioPreenchimento =
                    SystemClock.elapsedRealtimeNanos()

                preencherCampos(registro)

                val preenchimentoMs =
                    (SystemClock.elapsedRealtimeNanos() -
                            inicioPreenchimento) / 1_000_000.0

                dadosCarregados = true

                val totalMs =
                    (SystemClock.elapsedRealtimeNanos() - inicioTotal) /
                            1_000_000.0

                Log.i(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_CARREGAMENTO" +
                            " | origem=REPOSITORIO" +
                            " | registroId=$registroId" +
                            " | consultaMs=${"%.4f".format(java.util.Locale.US, consultaMs)}" +
                            " | preenchimentoMs=${"%.4f".format(java.util.Locale.US, preenchimentoMs)}" +
                            " | totalMs=${"%.4f".format(java.util.Locale.US, totalMs)}"
                )

            } catch (e: NoSuchElementException) {
                Log.w(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_CARREGAMENTO | erro=REGISTRO_NAO_ENCONTRADO",
                    e
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Registro não encontrado no servidor",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: IOException) {
                try {
                    val inicioLocal = SystemClock.elapsedRealtimeNanos()

                    val registroLocal =
                        repository.buscarLocalPorId(registroId)

                    if (registroLocal != null) {
                        preencherCampos(registroLocal)

                        val totalMs =
                            (SystemClock.elapsedRealtimeNanos() -
                                    inicioTotal) / 1_000_000.0

                        val consultaLocalMs =
                            (SystemClock.elapsedRealtimeNanos() -
                                    inicioLocal) / 1_000_000.0

                        Log.i(
                            TAG_DESEMPENHO,
                            "operacao=EDICAO_CARREGAMENTO" +
                                    " | origem=CACHE_LOCAL" +
                                    " | registroId=$registroId" +
                                    " | consultaLocalMs=${"%.4f".format(java.util.Locale.US, consultaLocalMs)}" +
                                    " | totalMs=${"%.4f".format(java.util.Locale.US, totalMs)}"
                        )

                        Toast.makeText(
                            this@EditarRegistroActivity,
                            "Exibindo dados locais. É necessário conectar à API para salvar.",
                            Toast.LENGTH_LONG
                        ).show()

                        dadosCarregados = true
                    } else {
                        Log.w(
                            TAG_DESEMPENHO,
                            "operacao=EDICAO_CARREGAMENTO | origem=CACHE_LOCAL | resultado=NAO_ENCONTRADO",
                            e
                        )

                        Toast.makeText(
                            this@EditarRegistroActivity,
                            "Não foi possível carregar o registro",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }
                } catch (erroLocal: Exception) {
                    Log.e(
                        TAG_DESEMPENHO,
                        "operacao=EDICAO_CARREGAMENTO | erro=FALHA_CACHE_LOCAL",
                        erroLocal
                    )

                    Toast.makeText(
                        this@EditarRegistroActivity,
                        "Não foi possível carregar o registro local",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }

            } catch (e: Exception) {
                Log.e(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_CARREGAMENTO | erro=INESPERADO",
                    e
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Erro ao carregar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } finally {
                carregando = false
                btnSalvarAlteracoes.isEnabled = dadosCarregados
            }
        }
    }

    private fun preencherCampos(registro: Registro) {
        edtTitulo.setText(registro.titulo)
        edtDescricao.setText(registro.descricao)

        val categorias =
            resources.getStringArray(R.array.categorias_registro)

        val categoriaIndex = categorias.indexOf(registro.categoria)

        if (categoriaIndex >= 0) {
            spinnerCategoria.setSelection(categoriaIndex)
        }

        edtValor.setText(registro.valor.toString())
        edtQuantidade.setText(registro.quantidade.toString())
    }

    private fun salvarAlteracoes() {
        if (salvando || !dadosCarregados) return

        val titulo = edtTitulo.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()
        val categoria = spinnerCategoria.selectedItem.toString()

        val valorTexto = edtValor.text.toString()
            .trim()
            .replace(",", ".")

        val quantidadeTexto = edtQuantidade.text.toString().trim()

        if (titulo.isEmpty()) {
            edtTitulo.error = "Digite o título"
            edtTitulo.requestFocus()
            return
        }

        if (descricao.isEmpty()) {
            edtDescricao.error = "Digite a descrição"
            edtDescricao.requestFocus()
            return
        }

        if (categoria.isEmpty()) {
            spinnerCategoria.requestFocus()
            return
        }

        val valor = valorTexto.toDoubleOrNull()

        if (valor == null || !valor.isFinite() || valor < 0.0) {
            edtValor.error = "Digite um valor válido"
            edtValor.requestFocus()
            return
        }

        val quantidade = quantidadeTexto.toIntOrNull()

        if (quantidade == null || quantidade < 0) {
            edtQuantidade.error = "Digite uma quantidade válida"
            edtQuantidade.requestFocus()
            return
        }

        salvando = true
        btnSalvarAlteracoes.isEnabled = false
        btnCancelar.isEnabled = false

        lifecycleScope.launch {
            val inicioAtualizacao = SystemClock.elapsedRealtimeNanos()

            try {
                val registroAtualizado = repository.editarRegistro(
                    id = registroId,
                    titulo = titulo,
                    descricao = descricao,
                    categoria = categoria,
                    valor = valor,
                    quantidade = quantidade
                )

                val totalMs =
                    (SystemClock.elapsedRealtimeNanos() -
                            inicioAtualizacao) / 1_000_000.0

                Log.i(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_SALVAMENTO" +
                            " | resultado=SUCESSO" +
                            " | registroId=$registroId" +
                            " | totalMs=${"%.4f".format(java.util.Locale.US, totalMs)}"
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Registro ${registroAtualizado.id} atualizado com sucesso!",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: NoSuchElementException) {
                Log.w(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_SALVAMENTO | resultado=REGISTRO_NAO_ENCONTRADO",
                    e
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Registro não encontrado no servidor",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: IOException) {
                Log.w(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_SALVAMENTO | resultado=ERRO_IO",
                    e
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Não foi possível confirmar a atualização. Verifique a API e sincronize antes de tentar novamente.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Log.e(
                    TAG_DESEMPENHO,
                    "operacao=EDICAO_SALVAMENTO | resultado=ERRO",
                    e
                )

                Toast.makeText(
                    this@EditarRegistroActivity,
                    "Erro ao atualizar: ${e.message}. Verifique os dados no servidor antes de tentar novamente.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                salvando = false
                btnSalvarAlteracoes.isEnabled = dadosCarregados
                btnCancelar.isEnabled = true
            }
        }
    }
}
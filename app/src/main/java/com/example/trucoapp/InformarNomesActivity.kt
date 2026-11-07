package com.example.trucoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged

class InformarNomesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOME_J1 = "EXTRA_NOME_J1"
        const val EXTRA_NOME_J2 = "EXTRA_NOME_J2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_informar_nomes)

        val dbHelper = TrucoDatabaseHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_informar_nomes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInformarNomes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val defaultJ1 = getString(R.string.jogador_1_default)
        val defaultJ2 = getString(R.string.jogador_2_default)
        val estadoSalvo = dbHelper.carregarEstado(defaultJ1, defaultJ2)

        val etNomeJogador1 = findViewById<EditText>(R.id.etNomeJogador1)
        val etNomeJogador2 = findViewById<EditText>(R.id.etNomeJogador2)
        val tvPreviewNomeJ1 = findViewById<TextView>(R.id.tvPreviewNomeJ1)
        val tvPreviewNomeJ2 = findViewById<TextView>(R.id.tvPreviewNomeJ2)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val btnRestaurarPadrao = findViewById<Button>(R.id.btnRestaurarPadrao)
        val btnVoltarNomes = findViewById<ImageButton>(R.id.btnVoltarNomes)
        val btnClearNomeJ1 = findViewById<ImageButton>(R.id.btnClearNomeJ1)
        val btnClearNomeJ2 = findViewById<ImageButton>(R.id.btnClearNomeJ2)

        val nomeJ1Atual = intent.getStringExtra(EXTRA_NOME_J1) ?: estadoSalvo.nomeJogador1
        val nomeJ2Atual = intent.getStringExtra(EXTRA_NOME_J2) ?: estadoSalvo.nomeJogador2

        if (nomeJ1Atual.isNotEmpty()) {
            etNomeJogador1.setText(nomeJ1Atual)
            tvPreviewNomeJ1.text = nomeJ1Atual
        }
        if (nomeJ2Atual.isNotEmpty()) {
            etNomeJogador2.setText(nomeJ2Atual)
            tvPreviewNomeJ2.text = nomeJ2Atual
        }

        etNomeJogador1.doAfterTextChanged { text ->
            val valor = text?.toString()?.trim().orEmpty()
            tvPreviewNomeJ1.text = valor.ifEmpty { defaultJ1 }
        }

        etNomeJogador2.doAfterTextChanged { text ->
            val valor = text?.toString()?.trim().orEmpty()
            tvPreviewNomeJ2.text = valor.ifEmpty { defaultJ2 }
        }

        btnClearNomeJ1.setOnClickListener {
            etNomeJogador1.setText("")
            etNomeJogador1.requestFocus()
        }

        btnClearNomeJ2.setOnClickListener {
            etNomeJogador2.setText("")
            etNomeJogador2.requestFocus()
        }

        fun aplicarPreset(nome1: String, nome2: String) {
            etNomeJogador1.setText(nome1)
            etNomeJogador2.setText(nome2)
        }

        findViewById<Button>(R.id.btnPresetNosEles).setOnClickListener {
            aplicarPreset("Nós", "Eles")
        }
        findViewById<Button>(R.id.btnPresetMesa).setOnClickListener {
            aplicarPreset("Mesa 1", "Mesa 2")
        }
        findViewById<Button>(R.id.btnPresetDupla).setOnClickListener {
            aplicarPreset("Dupla A", "Dupla B")
        }
        findViewById<Button>(R.id.btnPresetFamilia).setOnClickListener {
            aplicarPreset("Pai & Filho", "Tio & Primo")
        }

        btnRestaurarPadrao.setOnClickListener {
            aplicarPreset(defaultJ1, defaultJ2)
        }

        btnVoltarNomes.setOnClickListener {
            finish()
        }

        etNomeJogador1.setSelectAllOnFocus(true)
        etNomeJogador2.setSelectAllOnFocus(true)
        etNomeJogador1.requestFocus()

        btnConfirmar.setOnClickListener {
            val novoNomeJ1 = etNomeJogador1.text.toString().trim().ifEmpty {
                defaultJ1
            }
            val novoNomeJ2 = etNomeJogador2.text.toString().trim().ifEmpty {
                defaultJ2
            }

            dbHelper.atualizarNomes(novoNomeJ1, novoNomeJ2, defaultJ1, defaultJ2)

            val resultIntent = Intent().apply {
                putExtra(EXTRA_NOME_J1, novoNomeJ1)
                putExtra(EXTRA_NOME_J2, novoNomeJ2)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

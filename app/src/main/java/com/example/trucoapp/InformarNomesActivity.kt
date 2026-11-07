package com.example.trucoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InformarNomesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOME_J1 = "EXTRA_NOME_J1"
        const val EXTRA_NOME_J2 = "EXTRA_NOME_J2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_informar_nomes)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_informar_nomes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInformarNomes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etNomeJogador1 = findViewById<EditText>(R.id.etNomeJogador1)
        val etNomeJogador2 = findViewById<EditText>(R.id.etNomeJogador2)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)

        val nomeJ1Atual = intent.getStringExtra(EXTRA_NOME_J1) ?: ""
        val nomeJ2Atual = intent.getStringExtra(EXTRA_NOME_J2) ?: ""

        if (nomeJ1Atual.isNotEmpty()) {
            etNomeJogador1.setText(nomeJ1Atual)
        }
        if (nomeJ2Atual.isNotEmpty()) {
            etNomeJogador2.setText(nomeJ2Atual)
        }

        etNomeJogador1.setSelectAllOnFocus(true)
        etNomeJogador2.setSelectAllOnFocus(true)
        etNomeJogador1.requestFocus()

        btnConfirmar.setOnClickListener {
            val novoNomeJ1 = etNomeJogador1.text.toString().trim().ifEmpty {
                getString(R.string.jogador_1_default)
            }
            val novoNomeJ2 = etNomeJogador2.text.toString().trim().ifEmpty {
                getString(R.string.jogador_2_default)
            }

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
package com.example.trucoapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HistoricoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOME_J1 = "EXTRA_NOME_J1"
        const val EXTRA_VITORIAS_J1 = "EXTRA_VITORIAS_J1"
        const val EXTRA_NOME_J2 = "EXTRA_NOME_J2"
        const val EXTRA_VITORIAS_J2 = "EXTRA_VITORIAS_J2"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historico)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_historico)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHistorico)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nomeJ1 = intent.getStringExtra(EXTRA_NOME_J1) ?: getString(R.string.jogador_1_default)
        val vitoriasJ1 = intent.getIntExtra(EXTRA_VITORIAS_J1, 0)
        val nomeJ2 = intent.getStringExtra(EXTRA_NOME_J2) ?: getString(R.string.jogador_2_default)
        val vitoriasJ2 = intent.getIntExtra(EXTRA_VITORIAS_J2, 0)

        val tvStatusHistorico = findViewById<TextView>(R.id.tvStatusHistorico)
        val tvHistoricoJogador1 = findViewById<TextView>(R.id.tvHistoricoJogador1)
        val tvHistoricoJogador2 = findViewById<TextView>(R.id.tvHistoricoJogador2)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarHistorico)

        if (vitoriasJ1 > vitoriasJ2) {
            tvStatusHistorico.text = getString(R.string.format_lider_historico, nomeJ1)
        } else if (vitoriasJ2 > vitoriasJ1) {
            tvStatusHistorico.text = getString(R.string.format_lider_historico, nomeJ2)
        } else {
            tvStatusHistorico.text = getString(R.string.format_empate_historico)
        }

        tvHistoricoJogador1.text = getString(R.string.format_historico, nomeJ1, vitoriasJ1)
        tvHistoricoJogador2.text = getString(R.string.format_historico, nomeJ2, vitoriasJ2)

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
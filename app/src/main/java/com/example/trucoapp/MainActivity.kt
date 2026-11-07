package com.example.trucoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private val gameController = TrucoGameController()

    private lateinit var tvNomeJogador1: TextView
    private lateinit var tvPontosJogador1: TextView
    private lateinit var tvNomeJogador2: TextView
    private lateinit var tvPontosJogador2: TextView
    private lateinit var tvLiderJ1: TextView
    private lateinit var tvLiderJ2: TextView
    private lateinit var cardJogador1: MaterialCardView
    private lateinit var cardJogador2: MaterialCardView

    private val informarNomesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val novoNomeJ1 = data.getStringExtra(InformarNomesActivity.EXTRA_NOME_J1)
                val novoNomeJ2 = data.getStringExtra(InformarNomesActivity.EXTRA_NOME_J2)
                gameController.atualizarNomes(
                    novoNomeJ1,
                    novoNomeJ2,
                    getString(R.string.jogador_1_default),
                    getString(R.string.jogador_2_default)
                )
                atualizarInterface()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nomeDefault1 = getString(R.string.jogador_1_default)
        val nomeDefault2 = getString(R.string.jogador_2_default)

        if (savedInstanceState == null) {
            gameController.atualizarNomes(null, null, nomeDefault1, nomeDefault2)
        } else {
            val p1 = savedInstanceState.getInt("pontosJogador1", 0)
            val p2 = savedInstanceState.getInt("pontosJogador2", 0)
            val v1 = savedInstanceState.getInt("vitoriasJogador1", 0)
            val v2 = savedInstanceState.getInt("vitoriasJogador2", 0)
            val n1 = savedInstanceState.getString("nomeJogador1", nomeDefault1)
            val n2 = savedInstanceState.getString("nomeJogador2", nomeDefault2)
            gameController.restaurarEstado(p1, p2, v1, v2, n1, n2)
        }

        inicializarComponentes()
        configurarListeners()
        atualizarInterface()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pontosJogador1", gameController.pontosJogador1)
        outState.putInt("pontosJogador2", gameController.pontosJogador2)
        outState.putInt("vitoriasJogador1", gameController.vitoriasJogador1)
        outState.putInt("vitoriasJogador2", gameController.vitoriasJogador2)
        outState.putString("nomeJogador1", gameController.nomeJogador1)
        outState.putString("nomeJogador2", gameController.nomeJogador2)
    }

    private fun inicializarComponentes() {
        tvNomeJogador1 = findViewById(R.id.tvNomeJogador1)
        tvPontosJogador1 = findViewById(R.id.tvPontosJogador1)
        tvNomeJogador2 = findViewById(R.id.tvNomeJogador2)
        tvPontosJogador2 = findViewById(R.id.tvPontosJogador2)
        tvLiderJ1 = findViewById(R.id.tvLiderJ1)
        tvLiderJ2 = findViewById(R.id.tvLiderJ2)
        cardJogador1 = findViewById(R.id.cardJogador1)
        cardJogador2 = findViewById(R.id.cardJogador2)
    }

    private fun configurarListeners() {
        // Botões do Jogador 1
        findViewById<Button>(R.id.btnMinus1J1).setOnClickListener { subtrairPonto(1) }
        findViewById<Button>(R.id.btnPlus1J1).setOnClickListener { adicionarPontos(1, 1) }
        findViewById<Button>(R.id.btnPlus3J1).setOnClickListener { adicionarPontos(1, 3) }
        findViewById<Button>(R.id.btnPlus6J1).setOnClickListener { adicionarPontos(1, 6) }
        findViewById<Button>(R.id.btnPlus9J1).setOnClickListener { adicionarPontos(1, 9) }
        findViewById<Button>(R.id.btnPlus12J1).setOnClickListener { adicionarPontos(1, 12) }

        // Botões do Jogador 2
        findViewById<Button>(R.id.btnMinus1J2).setOnClickListener { subtrairPonto(2) }
        findViewById<Button>(R.id.btnPlus1J2).setOnClickListener { adicionarPontos(2, 1) }
        findViewById<Button>(R.id.btnPlus3J2).setOnClickListener { adicionarPontos(2, 3) }
        findViewById<Button>(R.id.btnPlus6J2).setOnClickListener { adicionarPontos(2, 6) }
        findViewById<Button>(R.id.btnPlus9J2).setOnClickListener { adicionarPontos(2, 9) }
        findViewById<Button>(R.id.btnPlus12J2).setOnClickListener { adicionarPontos(2, 12) }

        // Botões de Ação
        findViewById<Button>(R.id.btnZerarPartida).setOnClickListener { zerarPartida() }
        findViewById<Button>(R.id.btnZerarHistorico).setOnClickListener { zerarHistorico() }
        findViewById<Button>(R.id.btnHistorico).setOnClickListener { abrirHistorico() }
        findViewById<Button>(R.id.btnInformarNomes).setOnClickListener { abrirInformarNomes() }
    }

    private fun adicionarPontos(jogador: Int, pontos: Int) {
        val nomeVencedor = if (jogador == 1) gameController.nomeJogador1 else gameController.nomeJogador2
        val venceu = gameController.adicionarPontos(jogador, pontos)
        if (venceu) {
            exibirVencedor(nomeVencedor)
        }
        atualizarInterface()
    }

    private fun subtrairPonto(jogador: Int) {
        gameController.subtrairPonto(jogador)
        atualizarInterface()
    }

    private fun exibirVencedor(nomeVencedor: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_vitoria_titulo)
            .setMessage(getString(R.string.vencedor_mensagem, nomeVencedor))
            .setPositiveButton(R.string.dialog_vitoria_botao) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun zerarPartida() {
        gameController.resetarPontosPartida()
        atualizarInterface()
        Toast.makeText(this, getString(R.string.toast_partida_zerada), Toast.LENGTH_SHORT).show()
    }

    private fun zerarHistorico() {
        gameController.zerarHistorico(
            getString(R.string.jogador_1_default),
            getString(R.string.jogador_2_default)
        )
        atualizarInterface()
        Toast.makeText(this, getString(R.string.toast_historico_zerado), Toast.LENGTH_SHORT).show()
    }

    private fun abrirHistorico() {
        val intent = Intent(this, HistoricoActivity::class.java).apply {
            putExtra(HistoricoActivity.EXTRA_NOME_J1, gameController.nomeJogador1)
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J1, gameController.vitoriasJogador1)
            putExtra(HistoricoActivity.EXTRA_NOME_J2, gameController.nomeJogador2)
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J2, gameController.vitoriasJogador2)
        }
        startActivity(intent)
    }

    private fun abrirInformarNomes() {
        val intent = Intent(this, InformarNomesActivity::class.java).apply {
            putExtra(InformarNomesActivity.EXTRA_NOME_J1, gameController.nomeJogador1)
            putExtra(InformarNomesActivity.EXTRA_NOME_J2, gameController.nomeJogador2)
        }
        informarNomesLauncher.launch(intent)
    }

    private fun atualizarInterface() {
        tvNomeJogador1.text = getString(R.string.format_nome_label, gameController.nomeJogador1)
        tvPontosJogador1.text = gameController.pontosJogador1.toString()
        tvNomeJogador2.text = getString(R.string.format_nome_label, gameController.nomeJogador2)
        tvPontosJogador2.text = gameController.pontosJogador2.toString()

        val density = resources.displayMetrics.density
        val strokeWidthLider = (3 * density).toInt()
        val strokeWidthNormal = (1 * density).toInt()

        val colorLider = ContextCompat.getColor(this, R.color.card_leader_stroke)
        val colorJ1 = ContextCompat.getColor(this, R.color.card_j1_stroke)
        val colorJ2 = ContextCompat.getColor(this, R.color.card_j2_stroke)

        when (gameController.obterLiderPartida()) {
            1 -> {
                cardJogador1.strokeWidth = strokeWidthLider
                cardJogador1.strokeColor = colorLider
                tvLiderJ1.visibility = View.VISIBLE

                cardJogador2.strokeWidth = strokeWidthNormal
                cardJogador2.strokeColor = colorJ2
                tvLiderJ2.visibility = View.GONE
            }
            2 -> {
                cardJogador2.strokeWidth = strokeWidthLider
                cardJogador2.strokeColor = colorLider
                tvLiderJ2.visibility = View.VISIBLE

                cardJogador1.strokeWidth = strokeWidthNormal
                cardJogador1.strokeColor = colorJ1
                tvLiderJ1.visibility = View.GONE
            }
            else -> {
                cardJogador1.strokeWidth = strokeWidthNormal
                cardJogador1.strokeColor = colorJ1
                tvLiderJ1.visibility = View.GONE

                cardJogador2.strokeWidth = strokeWidthNormal
                cardJogador2.strokeColor = colorJ2
                tvLiderJ2.visibility = View.GONE
            }
        }
    }
}
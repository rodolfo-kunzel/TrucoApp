package com.example.trucoapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
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
    private lateinit var dbHelper: TrucoDatabaseHelper

    private lateinit var tvNomeJogador1: TextView
    private lateinit var tvPontosJogador1: TextView
    private lateinit var tvNomeJogador2: TextView
    private lateinit var tvPontosJogador2: TextView
    private lateinit var tvLiderJ1: TextView
    private lateinit var tvLiderJ2: TextView
    private lateinit var tvTentosJ1: TextView
    private lateinit var tvTentosJ2: TextView
    private lateinit var tvStatusRodada: TextView
    private lateinit var badgeIconContainerJ1: FrameLayout
    private lateinit var ivIconJ1: ImageView
    private lateinit var cardJogador1: MaterialCardView
    private lateinit var cardJogador2: MaterialCardView

    private val informarNomesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                val defaultJ1 = getString(R.string.jogador_1_default)
                val defaultJ2 = getString(R.string.jogador_2_default)
                val novoNomeJ1 = data.getStringExtra(InformarNomesActivity.EXTRA_NOME_J1)
                val novoNomeJ2 = data.getStringExtra(InformarNomesActivity.EXTRA_NOME_J2)
                gameController.atualizarNomes(
                    novoNomeJ1,
                    novoNomeJ2,
                    defaultJ1,
                    defaultJ2
                )
                dbHelper.salvarEstado(gameController)
                atualizarInterface()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = TrucoDatabaseHelper(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nomeDefault1 = getString(R.string.jogador_1_default)
        val nomeDefault2 = getString(R.string.jogador_2_default)
        val historicoSalvo = dbHelper.listarHistoricoPartidas()

        if (savedInstanceState == null) {
            val estadoBanco = dbHelper.carregarEstado(nomeDefault1, nomeDefault2)
            gameController.restaurarEstado(
                p1 = estadoBanco.pontosJogador1,
                p2 = estadoBanco.pontosJogador2,
                v1 = estadoBanco.vitoriasJogador1,
                v2 = estadoBanco.vitoriasJogador2,
                n1 = estadoBanco.nomeJogador1,
                n2 = estadoBanco.nomeJogador2,
                partidas = historicoSalvo
            )
        } else {
            val p1 = savedInstanceState.getInt("pontosJogador1", 0)
            val p2 = savedInstanceState.getInt("pontosJogador2", 0)
            val v1 = savedInstanceState.getInt("vitoriasJogador1", 0)
            val v2 = savedInstanceState.getInt("vitoriasJogador2", 0)
            val n1 = savedInstanceState.getString("nomeJogador1", nomeDefault1)
            val n2 = savedInstanceState.getString("nomeJogador2", nomeDefault2)
            gameController.restaurarEstado(p1, p2, v1, v2, n1, n2, historicoSalvo)
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
        tvTentosJ1 = findViewById(R.id.tvTentosJ1)
        tvTentosJ2 = findViewById(R.id.tvTentosJ2)
        tvStatusRodada = findViewById(R.id.tvStatusRodada)
        badgeIconContainerJ1 = findViewById(R.id.badgeIconContainerJ1)
        ivIconJ1 = findViewById(R.id.ivIconJ1)
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

        // Bottom Navigation
        findViewById<View>(R.id.navTabHistorico).setOnClickListener { abrirHistorico() }
    }

    private fun adicionarPontos(jogador: Int, pontos: Int) {
        val nomeVencedor = if (jogador == 1) gameController.nomeJogador1 else gameController.nomeJogador2
        val p1Antes = gameController.pontosJogador1
        val p2Antes = gameController.pontosJogador2

        val venceu = gameController.adicionarPontos(jogador, pontos)
        dbHelper.salvarEstado(gameController)

        if (venceu) {
            gameController.historicoPartidas.lastOrNull()?.let { ultimaPartida ->
                dbHelper.inserirPartidaHistorico(ultimaPartida)
            }
            val p1Final = if (jogador == 1) (p1Antes + pontos).coerceAtMost(12) else p1Antes
            val p2Final = if (jogador == 2) (p2Antes + pontos).coerceAtMost(12) else p2Antes
            exibirVencedor(nomeVencedor, p1Final, p2Final)
        }
        atualizarInterface()
    }

    private fun subtrairPonto(jogador: Int) {
        gameController.subtrairPonto(jogador)
        dbHelper.salvarEstado(gameController)
        atualizarInterface()
    }

    private fun exibirVencedor(nomeVencedor: String, p1Final: Int, p2Final: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_vitoria, null)
        val tvMensagem = dialogView.findViewById<TextView>(R.id.tvDialogVitoriaMensagem)
        val tvNomeJ1 = dialogView.findViewById<TextView>(R.id.tvDialogNomeJ1)
        val tvPontosJ1 = dialogView.findViewById<TextView>(R.id.tvDialogPontosJ1)
        val tvNomeJ2 = dialogView.findViewById<TextView>(R.id.tvDialogNomeJ2)
        val tvPontosJ2 = dialogView.findViewById<TextView>(R.id.tvDialogPontosJ2)
        val btnNovaPartida = dialogView.findViewById<Button>(R.id.btnDialogNovaPartida)

        tvMensagem.text = getString(R.string.vencedor_mensagem, nomeVencedor)
        tvNomeJ1.text = gameController.nomeJogador1
        tvPontosJ1.text = p1Final.toString()
        tvNomeJ2.text = gameController.nomeJogador2
        tvPontosJ2.text = p2Final.toString()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnNovaPartida.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun zerarPartida() {
        gameController.resetarPontosPartida()
        dbHelper.salvarEstado(gameController)
        atualizarInterface()
        Toast.makeText(this, getString(R.string.toast_partida_zerada), Toast.LENGTH_SHORT).show()
    }

    private fun zerarHistorico() {
        val defaultJ1 = getString(R.string.jogador_1_default)
        val defaultJ2 = getString(R.string.jogador_2_default)
        gameController.zerarHistorico(defaultJ1, defaultJ2)
        dbHelper.limparHistoricoERestaurarPadrao(defaultJ1, defaultJ2)
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

        tvTentosJ1.text = getString(R.string.format_tentos, gameController.vitoriasJogador1)
        tvTentosJ2.text = getString(R.string.format_tentos, gameController.vitoriasJogador2)

        if (gameController.pontosJogador1 == 11 || gameController.pontosJogador2 == 11) {
            tvStatusRodada.text = getString(R.string.status_mao_de_11)
        } else {
            tvStatusRodada.text = getString(R.string.status_rodada_andamento)
        }

        val density = resources.displayMetrics.density
        val strokeWidthLider = (2.5f * density).toInt()
        val strokeWidthNormal = (1 * density).toInt()

        val colorLider = ContextCompat.getColor(this, R.color.card_leader_stroke)
        val colorJ1 = ContextCompat.getColor(this, R.color.card_j1_stroke)
        val colorJ2 = ContextCompat.getColor(this, R.color.card_j2_stroke)

        val colorTertiaryFixed = ContextCompat.getColor(this, R.color.tertiary_fixed)
        val colorOnTertiaryFixed = ContextCompat.getColor(this, R.color.on_tertiary_fixed)
        val colorPrimaryFixed = ContextCompat.getColor(this, R.color.primary_fixed)
        val colorPrimary = ContextCompat.getColor(this, R.color.primary)

        when (gameController.obterLiderPartida()) {
            1 -> {
                cardJogador1.strokeWidth = strokeWidthLider
                cardJogador1.strokeColor = colorLider
                tvLiderJ1.visibility = View.VISIBLE
                badgeIconContainerJ1.backgroundTintList = ColorStateList.valueOf(colorTertiaryFixed)
                ivIconJ1.setImageResource(R.drawable.ic_trophy)
                ivIconJ1.imageTintList = ColorStateList.valueOf(colorOnTertiaryFixed)

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
                badgeIconContainerJ1.backgroundTintList = ColorStateList.valueOf(colorPrimaryFixed)
                ivIconJ1.setImageResource(R.drawable.ic_person)
                ivIconJ1.imageTintList = ColorStateList.valueOf(colorPrimary)
            }
            else -> {
                cardJogador1.strokeWidth = strokeWidthNormal
                cardJogador1.strokeColor = colorJ1
                tvLiderJ1.visibility = View.GONE
                badgeIconContainerJ1.backgroundTintList = ColorStateList.valueOf(colorTertiaryFixed)
                ivIconJ1.setImageResource(R.drawable.ic_trophy)
                ivIconJ1.imageTintList = ColorStateList.valueOf(colorOnTertiaryFixed)

                cardJogador2.strokeWidth = strokeWidthNormal
                cardJogador2.strokeColor = colorJ2
                tvLiderJ2.visibility = View.GONE
            }
        }
    }
}

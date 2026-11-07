package com.example.trucoapp

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs
import kotlin.math.roundToInt

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

        val dbHelper = TrucoDatabaseHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_historico)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHistorico)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val defaultJ1 = getString(R.string.jogador_1_default)
        val defaultJ2 = getString(R.string.jogador_2_default)
        val estadoBanco = dbHelper.carregarEstado(defaultJ1, defaultJ2)
        val partidasBanco = dbHelper.listarHistoricoPartidas()

        val nomeJ1 = intent.getStringExtra(EXTRA_NOME_J1) ?: estadoBanco.nomeJogador1
        val vitoriasJ1 = if (intent.hasExtra(EXTRA_VITORIAS_J1)) {
            intent.getIntExtra(EXTRA_VITORIAS_J1, 0)
        } else {
            estadoBanco.vitoriasJogador1
        }
        val nomeJ2 = intent.getStringExtra(EXTRA_NOME_J2) ?: estadoBanco.nomeJogador2
        val vitoriasJ2 = if (intent.hasExtra(EXTRA_VITORIAS_J2)) {
            intent.getIntExtra(EXTRA_VITORIAS_J2, 0)
        } else {
            estadoBanco.vitoriasJogador2
        }

        val tvStatusHistorico = findViewById<TextView>(R.id.tvStatusHistorico)
        val tvSubtituloStatusHistorico = findViewById<TextView>(R.id.tvSubtituloStatusHistorico)
        val tvTotalPartidasBadge = findViewById<TextView>(R.id.tvTotalPartidasBadge)
        val tvHistoricoJogador1 = findViewById<TextView>(R.id.tvHistoricoJogador1)
        val tvHistoricoJogador2 = findViewById<TextView>(R.id.tvHistoricoJogador2)
        val tvNomeHistoricoJ1 = findViewById<TextView>(R.id.tvNomeHistoricoJ1)
        val tvNomeHistoricoJ2 = findViewById<TextView>(R.id.tvNomeHistoricoJ2)
        val tvVitoriasCountJ1 = findViewById<TextView>(R.id.tvVitoriasCountJ1)
        val tvVitoriasCountJ2 = findViewById<TextView>(R.id.tvVitoriasCountJ2)
        val tvBadgeHistoricoJ1 = findViewById<TextView>(R.id.tvBadgeHistoricoJ1)
        val tvBadgeHistoricoJ2 = findViewById<TextView>(R.id.tvBadgeHistoricoJ2)
        val pbHistoricoJ1 = findViewById<ProgressBar>(R.id.pbHistoricoJ1)
        val pbHistoricoJ2 = findViewById<ProgressBar>(R.id.pbHistoricoJ2)
        val tvPercentJ1 = findViewById<TextView>(R.id.tvPercentJ1)
        val tvPercentJ2 = findViewById<TextView>(R.id.tvPercentJ2)
        val tvSemPartidasHistorico = findViewById<TextView>(R.id.tvSemPartidasHistorico)
        val llListaUltimasPartidas = findViewById<LinearLayout>(R.id.llListaUltimasPartidas)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarHistorico)

        val totalPartidas = vitoriasJ1 + vitoriasJ2
        tvTotalPartidasBadge.text = getString(R.string.format_partidas_badge, totalPartidas)

        val diff = abs(vitoriasJ1 - vitoriasJ2)
        when {
            vitoriasJ1 > vitoriasJ2 -> {
                tvStatusHistorico.text = getString(R.string.format_lider_historico, nomeJ1)
                tvSubtituloStatusHistorico.text = getString(R.string.format_vantagem_historico, diff)
                tvBadgeHistoricoJ1.visibility = View.VISIBLE
                tvBadgeHistoricoJ2.visibility = View.GONE
            }
            vitoriasJ2 > vitoriasJ1 -> {
                tvStatusHistorico.text = getString(R.string.format_lider_historico, nomeJ2)
                tvSubtituloStatusHistorico.text = getString(R.string.format_vantagem_historico, diff)
                tvBadgeHistoricoJ2.visibility = View.VISIBLE
                tvBadgeHistoricoJ1.visibility = View.GONE
            }
            else -> {
                tvStatusHistorico.text = getString(R.string.format_empate_historico)
                tvSubtituloStatusHistorico.text = getString(R.string.subtitulo_empate_historico)
                tvBadgeHistoricoJ1.visibility = View.GONE
                tvBadgeHistoricoJ2.visibility = View.GONE
            }
        }

        tvNomeHistoricoJ1.text = nomeJ1
        tvNomeHistoricoJ2.text = nomeJ2
        tvVitoriasCountJ1.text = vitoriasJ1.toString()
        tvVitoriasCountJ2.text = vitoriasJ2.toString()

        tvHistoricoJogador1.text = getString(R.string.format_historico, nomeJ1, vitoriasJ1)
        tvHistoricoJogador2.text = getString(R.string.format_historico, nomeJ2, vitoriasJ2)

        val pct1 = if (totalPartidas > 0) ((vitoriasJ1 * 100f) / totalPartidas).roundToInt() else 50
        val pct2 = if (totalPartidas > 0) 100 - pct1 else 50
        pbHistoricoJ1.progress = pct1
        pbHistoricoJ2.progress = pct2
        tvPercentJ1.text = getString(R.string.format_percent, pct1)
        tvPercentJ2.text = getString(R.string.format_percent, pct2)

        renderizarUltimasPartidas(
            partidas = partidasBanco,
            nomeJ1 = nomeJ1,
            nomeJ2 = nomeJ2,
            tvSemPartidas = tvSemPartidasHistorico,
            container = llListaUltimasPartidas
        )

        val finishListener = View.OnClickListener { finish() }
        btnVoltar.setOnClickListener(finishListener)
        findViewById<View>(R.id.navTabMarcadorHist).setOnClickListener(finishListener)
    }

    private fun renderizarUltimasPartidas(
        partidas: List<PartidaHistorico>,
        nomeJ1: String,
        nomeJ2: String,
        tvSemPartidas: TextView,
        container: LinearLayout
    ) {
        container.removeAllViews()
        if (partidas.isEmpty()) {
            tvSemPartidas.visibility = View.VISIBLE
            container.visibility = View.GONE
            return
        }

        tvSemPartidas.visibility = View.GONE
        container.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(this)
        val colorPrimary = ContextCompat.getColor(this, R.color.primary)
        val colorPrimaryFixed = ContextCompat.getColor(this, R.color.primary_fixed)
        val colorSecondary = ContextCompat.getColor(this, R.color.secondary)
        val colorSecondaryFixed = ContextCompat.getColor(this, R.color.secondary_fixed)
        val colorOutlineVariant = ContextCompat.getColor(this, R.color.outline_variant)

        for (partida in partidas) {
            val itemView = inflater.inflate(R.layout.item_partida_historico, container, false)
            val flIcone = itemView.findViewById<FrameLayout>(R.id.flIconePartidaItem)
            val ivIcone = itemView.findViewById<ImageView>(R.id.ivIconePartidaItem)
            val tvNumero = itemView.findViewById<TextView>(R.id.tvNumeroPartidaItem)
            val tvVencedor = itemView.findViewById<TextView>(R.id.tvVencedorPartidaItem)
            val tvDetalhe = itemView.findViewById<TextView>(R.id.tvDetalhePartidaItem)
            val tvPontosJ1 = itemView.findViewById<TextView>(R.id.tvPontosJ1Item)
            val tvPontosJ2 = itemView.findViewById<TextView>(R.id.tvPontosJ2Item)

            tvNumero.text = getString(R.string.format_partida_numero, partida.numeroPartida)
            tvPontosJ1.text = partida.pontosJogador1.toString()
            tvPontosJ2.text = partida.pontosJogador2.toString()

            tvDetalhe.text = when (partida.ultimoLancePontos) {
                1 -> getString(R.string.detalhe_vitoria_ponto)
                3 -> getString(R.string.detalhe_vitoria_truco)
                6 -> getString(R.string.detalhe_vitoria_seis)
                9 -> getString(R.string.detalhe_vitoria_nove)
                12 -> getString(R.string.detalhe_vitoria_doze)
                else -> getString(R.string.detalhe_vitoria_padrao, partida.ultimoLancePontos)
            }

            if (partida.vencedorJogador == 1) {
                tvVencedor.text = nomeJ1
                flIcone.backgroundTintList = ColorStateList.valueOf(colorPrimaryFixed)
                ivIcone.imageTintList = ColorStateList.valueOf(colorPrimary)
                tvPontosJ1.setTextColor(colorPrimary)
                tvPontosJ2.setTextColor(colorOutlineVariant)
            } else {
                tvVencedor.text = nomeJ2
                flIcone.backgroundTintList = ColorStateList.valueOf(colorSecondaryFixed)
                ivIcone.imageTintList = ColorStateList.valueOf(colorSecondary)
                tvPontosJ1.setTextColor(colorOutlineVariant)
                tvPontosJ2.setTextColor(colorSecondary)
            }

            container.addView(itemView)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

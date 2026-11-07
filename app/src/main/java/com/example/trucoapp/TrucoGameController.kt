package com.example.trucoapp

import java.io.Serializable

data class PartidaHistorico(
    val numeroPartida: Int,
    val vencedorJogador: Int,
    val pontosJogador1: Int,
    val pontosJogador2: Int,
    val ultimoLancePontos: Int
) : Serializable

class TrucoGameController(
    var nomeJogador1: String = "Jogador 1",
    var nomeJogador2: String = "Jogador 2"
) {
    var pontosJogador1: Int = 0
        private set
    var pontosJogador2: Int = 0
        private set
    var vitoriasJogador1: Int = 0
        private set
    var vitoriasJogador2: Int = 0
        private set

    private val _historicoPartidas = ArrayList<PartidaHistorico>()
    val historicoPartidas: List<PartidaHistorico>
        get() = _historicoPartidas

    fun adicionarPontos(jogador: Int, pontos: Int): Boolean {
        if (jogador == 1) {
            pontosJogador1 += pontos
            if (pontosJogador1 >= 12) {
                vitoriasJogador1++
                val numero = vitoriasJogador1 + vitoriasJogador2
                _historicoPartidas.add(
                    PartidaHistorico(
                        numeroPartida = numero,
                        vencedorJogador = 1,
                        pontosJogador1 = 12,
                        pontosJogador2 = pontosJogador2,
                        ultimoLancePontos = pontos
                    )
                )
                resetarPontosPartida()
                return true
            }
        } else if (jogador == 2) {
            pontosJogador2 += pontos
            if (pontosJogador2 >= 12) {
                vitoriasJogador2++
                val numero = vitoriasJogador1 + vitoriasJogador2
                _historicoPartidas.add(
                    PartidaHistorico(
                        numeroPartida = numero,
                        vencedorJogador = 2,
                        pontosJogador1 = pontosJogador1,
                        pontosJogador2 = 12,
                        ultimoLancePontos = pontos
                    )
                )
                resetarPontosPartida()
                return true
            }
        }
        return false
    }

    fun subtrairPonto(jogador: Int) {
        if (jogador == 1 && pontosJogador1 > 0) {
            pontosJogador1--
        } else if (jogador == 2 && pontosJogador2 > 0) {
            pontosJogador2--
        }
    }

    fun resetarPontosPartida() {
        pontosJogador1 = 0
        pontosJogador2 = 0
    }

    fun zerarHistorico(nomeDefault1: String = "Jogador 1", nomeDefault2: String = "Jogador 2") {
        resetarPontosPartida()
        vitoriasJogador1 = 0
        vitoriasJogador2 = 0
        nomeJogador1 = nomeDefault1
        nomeJogador2 = nomeDefault2
        _historicoPartidas.clear()
    }

    fun atualizarNomes(novoNome1: String?, novoNome2: String?, nomeDefault1: String, nomeDefault2: String) {
        nomeJogador1 = novoNome1?.trim()?.ifEmpty { nomeDefault1 } ?: nomeDefault1
        nomeJogador2 = novoNome2?.trim()?.ifEmpty { nomeDefault2 } ?: nomeDefault2
    }

    fun obterLiderPartida(): Int {
        return when {
            pontosJogador1 > pontosJogador2 && pontosJogador1 > 0 -> 1
            pontosJogador2 > pontosJogador1 && pontosJogador2 > 0 -> 2
            else -> 0
        }
    }

    fun obterLiderHistorico(): Int {
        return when {
            vitoriasJogador1 > vitoriasJogador2 -> 1
            vitoriasJogador2 > vitoriasJogador1 -> 2
            else -> 0
        }
    }

    fun restaurarEstado(
        p1: Int,
        p2: Int,
        v1: Int,
        v2: Int,
        n1: String,
        n2: String,
        partidas: List<PartidaHistorico> = emptyList()
    ) {
        pontosJogador1 = p1
        pontosJogador2 = p2
        vitoriasJogador1 = v1
        vitoriasJogador2 = v2
        nomeJogador1 = n1
        nomeJogador2 = n2
        _historicoPartidas.clear()
        _historicoPartidas.addAll(partidas)
    }
}

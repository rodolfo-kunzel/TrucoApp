package com.example.trucoapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TrucoGameLogicTest {

    private lateinit var controller: TrucoGameController

    @Before
    fun setup() {
        controller = TrucoGameController("Especialistas", "Desafiantes")
    }

    @Test
    fun estadoInicial_pontosEVitoriasZerados() {
        assertEquals(0, controller.pontosJogador1)
        assertEquals(0, controller.pontosJogador2)
        assertEquals(0, controller.vitoriasJogador1)
        assertEquals(0, controller.vitoriasJogador2)
        assertEquals("Especialistas", controller.nomeJogador1)
        assertEquals("Desafiantes", controller.nomeJogador2)
        assertTrue(controller.historicoPartidas.isEmpty())
    }

    @Test
    fun adicionarPontos_incrementaPontuacao() {
        assertFalse(controller.adicionarPontos(1, 1))
        assertEquals(1, controller.pontosJogador1)

        assertFalse(controller.adicionarPontos(1, 3))
        assertEquals(4, controller.pontosJogador1)

        assertFalse(controller.adicionarPontos(2, 6))
        assertEquals(6, controller.pontosJogador2)
    }

    @Test
    fun subtrairPontos_decrementaPontosSemFicarNegativo() {
        controller.adicionarPontos(1, 3)
        assertEquals(3, controller.pontosJogador1)

        controller.subtrairPonto(1)
        assertEquals(2, controller.pontosJogador1)

        // Subtrai até 0 e garante que não fica negativo
        controller.subtrairPonto(1)
        controller.subtrairPonto(1)
        controller.subtrairPonto(1)
        assertEquals(0, controller.pontosJogador1)
    }

    @Test
    fun jogador1VenceAoAtingir12Pontos_eRegistraPartidaNoHistorico() {
        controller.adicionarPontos(2, 8)
        controller.adicionarPontos(1, 9)
        assertFalse(controller.adicionarPontos(1, 1)) // 10 pontos
        assertEquals(10, controller.pontosJogador1)

        val venceu = controller.adicionarPontos(1, 3) // 13 pontos (>= 12)
        assertTrue(venceu)
        assertEquals(1, controller.vitoriasJogador1)
        assertEquals(0, controller.pontosJogador1)
        assertEquals(0, controller.pontosJogador2)

        assertEquals(1, controller.historicoPartidas.size)
        val partida = controller.historicoPartidas.first()
        assertEquals(1, partida.numeroPartida)
        assertEquals(1, partida.vencedorJogador)
        assertEquals(12, partida.pontosJogador1)
        assertEquals(8, partida.pontosJogador2)
        assertEquals(3, partida.ultimoLancePontos)
    }

    @Test
    fun jogador2VenceAoAtingir12Pontos() {
        val venceu = controller.adicionarPontos(2, 12)
        assertTrue(venceu)
        assertEquals(1, controller.vitoriasJogador2)
        assertEquals(0, controller.pontosJogador1)
        assertEquals(0, controller.pontosJogador2)
        assertEquals(1, controller.historicoPartidas.size)
        assertEquals(2, controller.historicoPartidas.first().vencedorJogador)
    }

    @Test
    fun zerarPartida_resetaPontosMantendoVitoriasENomes() {
        controller.adicionarPontos(1, 12) // J1 ganha 1 partida
        controller.adicionarPontos(1, 5)
        controller.adicionarPontos(2, 8)

        controller.resetarPontosPartida()

        assertEquals(0, controller.pontosJogador1)
        assertEquals(0, controller.pontosJogador2)
        assertEquals(1, controller.vitoriasJogador1)
        assertEquals("Especialistas", controller.nomeJogador1)
        assertEquals(1, controller.historicoPartidas.size)
    }

    @Test
    fun zerarHistorico_resetaPontosVitoriasNomesEPartidas() {
        controller.adicionarPontos(1, 12) // J1 ganha
        controller.adicionarPontos(2, 6)

        controller.zerarHistorico("Jogador 1", "Jogador 2")

        assertEquals(0, controller.pontosJogador1)
        assertEquals(0, controller.pontosJogador2)
        assertEquals(0, controller.vitoriasJogador1)
        assertEquals(0, controller.vitoriasJogador2)
        assertEquals("Jogador 1", controller.nomeJogador1)
        assertEquals("Jogador 2", controller.nomeJogador2)
        assertTrue(controller.historicoPartidas.isEmpty())
    }

    @Test
    fun obterLiderPartida_identificaLiderCorreto() {
        assertEquals(0, controller.obterLiderPartida()) // Empate inicial 0x0

        controller.adicionarPontos(1, 3)
        assertEquals(1, controller.obterLiderPartida()) // J1 lidera

        controller.adicionarPontos(2, 6)
        assertEquals(2, controller.obterLiderPartida()) // J2 lidera
    }

    @Test
    fun obterLiderHistorico_identificaLiderHistorico() {
        assertEquals(0, controller.obterLiderHistorico()) // Empate

        controller.adicionarPontos(1, 12) // J1 vence 1a partida
        assertEquals(1, controller.obterLiderHistorico())

        controller.adicionarPontos(2, 12) // J2 vence 1a partida
        controller.adicionarPontos(2, 12) // J2 vence 2a partida
        assertEquals(2, controller.obterLiderHistorico())
    }

    @Test
    fun atualizarNomes_trataNomesVaziosComFallback() {
        controller.atualizarNomes("   ", "", "Padrao 1", "Padrao 2")

        assertEquals("Padrao 1", controller.nomeJogador1)
        assertEquals("Padrao 2", controller.nomeJogador2)

        controller.atualizarNomes("Novo Time 1", "Novo Time 2", "Padrao 1", "Padrao 2")
        assertEquals("Novo Time 1", controller.nomeJogador1)
        assertEquals("Novo Time 2", controller.nomeJogador2)
    }
}

package com.example.trucoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun telaPrincipal_exibeComponentesIniciais() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tvTituloMain)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("0")))
            onView(withId(R.id.tvPontosJogador2)).check(matches(withText("0")))
            onView(withId(R.id.btnPlus1J1)).check(matches(isDisplayed()))
            onView(withId(R.id.btnHistorico)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun somarPontosJogador1_atualizaTexto() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlus1J1)).perform(click())
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("1")))

            onView(withId(R.id.btnPlus3J1)).perform(click())
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("4")))
        }
    }

    @Test
    fun subtrairPontoJogador1_decrementaPontuacao() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlus3J1)).perform(click())
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("3")))

            onView(withId(R.id.btnMinus1J1)).perform(click())
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("2")))
        }
    }

    @Test
    fun liderApareceEInverteConformePontuacao() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Inicialmente sem coroa de líder
            onView(withId(R.id.tvLiderJ1)).check(matches(not(isDisplayed())))
            onView(withId(R.id.tvLiderJ2)).check(matches(not(isDisplayed())))

            // Jogador 1 pontua -> Coroa no J1
            onView(withId(R.id.btnPlus3J1)).perform(click())
            onView(withId(R.id.tvLiderJ1)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLiderJ2)).check(matches(not(isDisplayed())))

            // Jogador 2 pontua +6 -> Coroa no J2
            onView(withId(R.id.btnPlus6J2)).perform(click())
            onView(withId(R.id.tvLiderJ2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvLiderJ1)).check(matches(not(isDisplayed())))

            // Empate 6x6 -> Nenhuma coroa visível
            onView(withId(R.id.btnPlus3J1)).perform(click())
            onView(withId(R.id.tvLiderJ1)).check(matches(not(isDisplayed())))
            onView(withId(R.id.tvLiderJ2)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun atingir12Pontos_exibeDialogDeVitoriaEIniciaNovaPartida() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlus12J1)).perform(click())

            // Valida exibição do AlertDialog de Vitória
            onView(withText("🏆 Partida Finalizada!")).check(matches(isDisplayed()))

            // Clica no botão "Iniciar Nova Partida"
            onView(withText("Iniciar Nova Partida")).perform(click())

            // Valida reset de pontuação para 0
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("0")))
        }
    }

    @Test
    fun zerarPartida_resetaPontuacaoParaZero() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlus6J1)).perform(click())
            onView(withId(R.id.btnPlus3J2)).perform(click())

            onView(withId(R.id.btnZerarPartida)).perform(click())

            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("0")))
            onView(withId(R.id.tvPontosJogador2)).check(matches(withText("0")))
        }
    }

    @Test
    fun fluxoCompleto_alterarNomesEAtualizarTelaPrincipal() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Clica para abrir tela de Informar Nomes
            onView(withId(R.id.btnInformarNomes)).perform(click())

            // Digita os novos nomes
            onView(withId(R.id.etNomeJogador1)).perform(clearText(), typeText("Time Alfa"), closeSoftKeyboard())
            onView(withId(R.id.etNomeJogador2)).perform(clearText(), typeText("Time Beta"), closeSoftKeyboard())

            // Confirma
            onView(withId(R.id.btnConfirmar)).perform(click())

            // Verifica se a Tela Principal atualizou os nomes das equipes
            onView(withId(R.id.tvNomeJogador1)).check(matches(withText("Time Alfa: ")))
            onView(withId(R.id.tvNomeJogador2)).check(matches(withText("Time Beta: ")))
        }
    }

    @Test
    fun fluxoCompleto_zerarHistorico_resetaNomesEPontos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Altera nome para Time Alfa
            onView(withId(R.id.btnInformarNomes)).perform(click())
            onView(withId(R.id.etNomeJogador1)).perform(clearText(), typeText("Time Alfa"), closeSoftKeyboard())
            onView(withId(R.id.btnConfirmar)).perform(click())

            // Clica em Zerar Histórico
            onView(withId(R.id.btnZerarHistorico)).perform(click())

            // Valida retorno para nomes padrão
            onView(withId(R.id.tvNomeJogador1)).check(matches(withText("Jogador 1: ")))
        }
    }

    @Test
    fun recriarActivity_preservaEstadoDPontuacao() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.btnPlus3J1)).perform(click())
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("3")))

            // Simula rotação de tela / recriação da Activity
            scenario.recreate()

            // Valida se os pontos se mantiveram
            onView(withId(R.id.tvPontosJogador1)).check(matches(withText("3")))
        }
    }

    @Test
    fun navegarParaHistorico_exibeTelaHistorico() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnHistorico)).perform(click())
            onView(withId(R.id.tvTituloHistorico)).check(matches(isDisplayed()))
        }
    }
}
package com.example.trucoapp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoricoActivityTest {

    @Test
    fun historicoActivity_exibeExtrasERecuperaLider() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), HistoricoActivity::class.java).apply {
            putExtra(HistoricoActivity.EXTRA_NOME_J1, "Os Feras")
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J1, 5)
            putExtra(HistoricoActivity.EXTRA_NOME_J2, "Os Craques")
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J2, 2)
        }

        ActivityScenario.launch<HistoricoActivity>(intent).use {
            onView(withId(R.id.tvTituloHistorico)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStatusHistorico)).check(matches(withText(containsString("Os Feras"))))
            onView(withId(R.id.tvHistoricoJogador1)).check(matches(withText(containsString("Os Feras: 5"))))
            onView(withId(R.id.tvHistoricoJogador2)).check(matches(withText(containsString("Os Craques: 2"))))
            onView(withId(R.id.btnVoltarHistorico)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun historicoActivity_quandoEmpatado_exibeStatusEmpate() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), HistoricoActivity::class.java).apply {
            putExtra(HistoricoActivity.EXTRA_NOME_J1, "Time A")
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J1, 3)
            putExtra(HistoricoActivity.EXTRA_NOME_J2, "Time B")
            putExtra(HistoricoActivity.EXTRA_VITORIAS_J2, 3)
        }

        ActivityScenario.launch<HistoricoActivity>(intent).use {
            onView(withId(R.id.tvStatusHistorico)).check(matches(withText(containsString("Empatado"))))
        }
    }

    @Test
    fun clicarBotaoVoltar_finalizaActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), HistoricoActivity::class.java)
        ActivityScenario.launch<HistoricoActivity>(intent).use {
            onView(withId(R.id.btnVoltarHistorico)).perform(click())
        }
    }
}
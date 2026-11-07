package com.example.trucoapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class EstadoJogoSalvo(
    val nomeJogador1: String,
    val nomeJogador2: String,
    val pontosJogador1: Int,
    val pontosJogador2: Int,
    val vitoriasJogador1: Int,
    val vitoriasJogador2: Int
)

class TrucoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "truco_app.db"
        const val DATABASE_VERSION = 1

        // Tabela de Estado Atual do Jogo
        const val TABLE_ESTADO = "estado_jogo"
        const val COL_ESTADO_ID = "id"
        const val COL_NOME_J1 = "nome_jogador1"
        const val COL_NOME_J2 = "nome_jogador2"
        const val COL_PONTOS_J1 = "pontos_jogador1"
        const val COL_PONTOS_J2 = "pontos_jogador2"
        const val COL_VITORIAS_J1 = "vitorias_jogador1"
        const val COL_VITORIAS_J2 = "vitorias_jogador2"

        // Tabela de Histórico de Partidas Finalizadas
        const val TABLE_HISTORICO = "historico_partidas"
        const val COL_HIST_ID = "id"
        const val COL_NUMERO_PARTIDA = "numero_partida"
        const val COL_VENCEDOR_JOGADOR = "vencedor_jogador"
        const val COL_HIST_PONTOS_J1 = "pontos_jogador1"
        const val COL_HIST_PONTOS_J2 = "pontos_jogador2"
        const val COL_ULTIMO_LANCE = "ultimo_lance_pontos"
        const val COL_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createEstadoTable = """
            CREATE TABLE $TABLE_ESTADO (
                $COL_ESTADO_ID INTEGER PRIMARY KEY,
                $COL_NOME_J1 TEXT NOT NULL,
                $COL_NOME_J2 TEXT NOT NULL,
                $COL_PONTOS_J1 INTEGER NOT NULL DEFAULT 0,
                $COL_PONTOS_J2 INTEGER NOT NULL DEFAULT 0,
                $COL_VITORIAS_J1 INTEGER NOT NULL DEFAULT 0,
                $COL_VITORIAS_J2 INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        val createHistoricoTable = """
            CREATE TABLE $TABLE_HISTORICO (
                $COL_HIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NUMERO_PARTIDA INTEGER NOT NULL,
                $COL_VENCEDOR_JOGADOR INTEGER NOT NULL,
                $COL_HIST_PONTOS_J1 INTEGER NOT NULL,
                $COL_HIST_PONTOS_J2 INTEGER NOT NULL,
                $COL_ULTIMO_LANCE INTEGER NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createEstadoTable)
        db.execSQL(createHistoricoTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ESTADO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORICO")
        onCreate(db)
    }

    fun carregarEstado(defaultNome1: String, defaultNome2: String): EstadoJogoSalvo {
        val db = readableDatabase
        db.query(
            TABLE_ESTADO,
            null,
            "$COL_ESTADO_ID = ?",
            arrayOf("1"),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return EstadoJogoSalvo(
                    nomeJogador1 = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME_J1)),
                    nomeJogador2 = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOME_J2)),
                    pontosJogador1 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PONTOS_J1)),
                    pontosJogador2 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PONTOS_J2)),
                    vitoriasJogador1 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VITORIAS_J1)),
                    vitoriasJogador2 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VITORIAS_J2))
                )
            }
        }

        val estadoInicial = EstadoJogoSalvo(
            nomeJogador1 = defaultNome1,
            nomeJogador2 = defaultNome2,
            pontosJogador1 = 0,
            pontosJogador2 = 0,
            vitoriasJogador1 = 0,
            vitoriasJogador2 = 0
        )
        salvarEstado(estadoInicial)
        return estadoInicial
    }

    fun salvarEstado(estado: EstadoJogoSalvo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ESTADO_ID, 1)
            put(COL_NOME_J1, estado.nomeJogador1)
            put(COL_NOME_J2, estado.nomeJogador2)
            put(COL_PONTOS_J1, estado.pontosJogador1)
            put(COL_PONTOS_J2, estado.pontosJogador2)
            put(COL_VITORIAS_J1, estado.vitoriasJogador1)
            put(COL_VITORIAS_J2, estado.vitoriasJogador2)
        }
        db.insertWithOnConflict(TABLE_ESTADO, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun salvarEstado(controller: TrucoGameController) {
        salvarEstado(
            EstadoJogoSalvo(
                nomeJogador1 = controller.nomeJogador1,
                nomeJogador2 = controller.nomeJogador2,
                pontosJogador1 = controller.pontosJogador1,
                pontosJogador2 = controller.pontosJogador2,
                vitoriasJogador1 = controller.vitoriasJogador1,
                vitoriasJogador2 = controller.vitoriasJogador2
            )
        )
    }

    fun atualizarNomes(novoNome1: String, novoNome2: String, defaultNome1: String, defaultNome2: String) {
        val estadoAtual = carregarEstado(defaultNome1, defaultNome2)
        salvarEstado(
            estadoAtual.copy(
                nomeJogador1 = novoNome1,
                nomeJogador2 = novoNome2
            )
        )
    }

    fun inserirPartidaHistorico(partida: PartidaHistorico) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NUMERO_PARTIDA, partida.numeroPartida)
            put(COL_VENCEDOR_JOGADOR, partida.vencedorJogador)
            put(COL_HIST_PONTOS_J1, partida.pontosJogador1)
            put(COL_HIST_PONTOS_J2, partida.pontosJogador2)
            put(COL_ULTIMO_LANCE, partida.ultimoLancePontos)
            put(COL_TIMESTAMP, System.currentTimeMillis())
        }
        db.insert(TABLE_HISTORICO, null, values)
    }

    fun listarHistoricoPartidas(): List<PartidaHistorico> {
        val lista = ArrayList<PartidaHistorico>()
        val db = readableDatabase
        db.query(
            TABLE_HISTORICO,
            null,
            null,
            null,
            null,
            null,
            "$COL_NUMERO_PARTIDA DESC, $COL_HIST_ID DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    PartidaHistorico(
                        numeroPartida = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMERO_PARTIDA)),
                        vencedorJogador = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VENCEDOR_JOGADOR)),
                        pontosJogador1 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIST_PONTOS_J1)),
                        pontosJogador2 = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIST_PONTOS_J2)),
                        ultimoLancePontos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ULTIMO_LANCE))
                    )
                )
            }
        }
        return lista
    }

    fun limparHistoricoERestaurarPadrao(defaultNome1: String, defaultNome2: String) {
        val db = writableDatabase
        db.delete(TABLE_HISTORICO, null, null)
        salvarEstado(
            EstadoJogoSalvo(
                nomeJogador1 = defaultNome1,
                nomeJogador2 = defaultNome2,
                pontosJogador1 = 0,
                pontosJogador2 = 0,
                vitoriasJogador1 = 0,
                vitoriasJogador2 = 0
            )
        )
    }
}

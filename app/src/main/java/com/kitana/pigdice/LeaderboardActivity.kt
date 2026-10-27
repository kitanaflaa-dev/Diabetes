package com.kitana.pigdice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

class LeaderboardActivity : AppCompatActivity() {

    companion object {
        private const val FILE_NAME = "pig_leaderboard.txt"
        const val EXTRA_WINNER_NAME = "winnerName"
        const val EXTRA_WINNER_SCORE = "winnerScore"
        const val EXTRA_WINNER_DATE = "winnerDate"
    }

    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var btnPlayGame: Button
    private lateinit var tvTitle: TextView
    private val records = mutableListOf<GameRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        tvTitle = findViewById(R.id.tvLeaderboardTitle)
        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        btnPlayGame = findViewById(R.id.btnPlayGame)

        rvLeaderboard.layoutManager = LinearLayoutManager(this)

        // If we were started with winner data, append to file
        handleIntentExtras()

        // Load all records from file
        loadRecordsFromFile()

        if (records.isEmpty()) {
            // show a single "empty" row
            records.add(GameRecord(
                winner = "",
                score = -1,
                date = getString(R.string.leaderboard_empty)
            ))
        }

        rvLeaderboard.adapter = LeaderboardAdapter(records)

        btnPlayGame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleIntentExtras() {
        val winner = intent.getStringExtra(EXTRA_WINNER_NAME)
        val score = intent.getIntExtra(EXTRA_WINNER_SCORE, -1)
        val date = intent.getStringExtra(EXTRA_WINNER_DATE)

        if (winner != null && score >= 0 && date != null) {
            val line = "$winner|$score|$date\n"
            openFileOutput(FILE_NAME, MODE_APPEND).use { fos ->
                fos.write(line.toByteArray())
            }
        }
    }

    private fun loadRecordsFromFile() {
        records.clear()
        try {
            openFileInput(FILE_NAME).use { fis ->
                BufferedReader(InputStreamReader(fis)).use { reader ->
                    reader.lineSequence()
                        .filter { it.isNotBlank() }
                        .forEach { line ->
                            val parts = line.split("|")
                            if (parts.size == 3) {
                                val winner = parts[0]
                                val score = parts[1].toIntOrNull() ?: -1
                                val date = parts[2]
                                records.add(GameRecord(winner, score, date))
                            }
                        }
                }
            }
            // Newest first (we append, so last line is newest)
            records.reverse()
        } catch (e: FileNotFoundException) {
            // No file yet – ignore, handled by empty check
        }
    }
}

data class GameRecord(
    val winner: String,
    val score: Int,
    val date: String
)

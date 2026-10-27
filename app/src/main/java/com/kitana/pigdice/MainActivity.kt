package com.kitana.pigdice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var tvWho: TextView
    private lateinit var tvGamesWon: TextView
    private lateinit var tvRollSum: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvTurn: TextView
    private lateinit var tvTapStart: TextView
    private lateinit var imgDie1: ImageView
    private lateinit var imgDie2: ImageView
    private lateinit var imgResult: ImageView
    private lateinit var btnRoll: Button
    private lateinit var btnHold: Button
    private lateinit var btnLeaderboard: Button

    private var playerTotal = 0
    private var computerTotal = 0
    private var turnScore = 0
    private var gamesWonPlayer = 0
    private var gamesWonComputer = 0
    private var playerTurn = true
    private var gameOver = false
    private var computerRolls = 0
    private var lastDie1 = 1
    private var lastDie2 = 1
    private var currentResultImageId = 0

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvWho = findViewById(R.id.tvWho)
        tvGamesWon = findViewById(R.id.tvGamesWon)
        tvRollSum = findViewById(R.id.tvRollSum)
        tvTotal = findViewById(R.id.tvTotalScore)
        tvTurn = findViewById(R.id.tvTurnScore)
        tvTapStart = findViewById(R.id.tvTapStart)
        imgDie1 = findViewById(R.id.imgDie1)
        imgDie2 = findViewById(R.id.imgDie2)
        imgResult = findViewById(R.id.imgResult)
        btnRoll = findViewById(R.id.btnRoll)
        btnHold = findViewById(R.id.btnHold)
        btnLeaderboard = findViewById(R.id.btnLeaderboard)

        btnRoll.setOnClickListener { onRoll() }
        btnHold.setOnClickListener { onHold() }
        tvTapStart.setOnClickListener { newGame() }
        btnLeaderboard.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        if (savedInstanceState != null) {
            playerTotal = savedInstanceState.getInt("playerTotal")
            computerTotal = savedInstanceState.getInt("computerTotal")
            turnScore = savedInstanceState.getInt("turnScore")
            lastDie1 = savedInstanceState.getInt("lastDie1")
            lastDie2 = savedInstanceState.getInt("lastDie2")
            playerTurn = savedInstanceState.getBoolean("playerTurn")
            gameOver = savedInstanceState.getBoolean("gameOver")
            gamesWonPlayer = savedInstanceState.getInt("gamesWonPlayer")
            gamesWonComputer = savedInstanceState.getInt("gamesWonComputer")

            updateHeader()
            updateTotals()
            setDice(lastDie1, lastDie2)

            if (savedInstanceState.getBoolean("resultVisible")) {
                val id = savedInstanceState.getInt("resultImageId")
                if (id != 0) {
                    currentResultImageId = id
                    imgResult.setImageResource(id)
                    imgResult.visibility = View.VISIBLE
                    tvTapStart.visibility = View.VISIBLE
                    btnRoll.isEnabled = false
                    btnHold.isEnabled = false
                }
            }
        } else {
            newGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("playerTotal", playerTotal)
        outState.putInt("computerTotal", computerTotal)
        outState.putInt("turnScore", turnScore)
        outState.putInt("lastDie1", lastDie1)
        outState.putInt("lastDie2", lastDie2)
        outState.putBoolean("playerTurn", playerTurn)
        outState.putBoolean("gameOver", gameOver)
        outState.putInt("gamesWonPlayer", gamesWonPlayer)
        outState.putInt("gamesWonComputer", gamesWonComputer)
        outState.putBoolean("resultVisible", imgResult.visibility == View.VISIBLE)
        outState.putInt("resultImageId", currentResultImageId)
    }

    private fun newGame() {
        handler.removeCallbacksAndMessages(null)

        playerTotal = 0; computerTotal = 0; turnScore = 0; computerRolls = 0
        playerTurn = true; gameOver = false
        currentResultImageId = 0
        imgResult.visibility = View.GONE
        tvTapStart.visibility = View.GONE
        updateHeader(); updateTotals()
        setDice(1,1)
        btnRoll.isEnabled = true
        btnHold.isEnabled = true
    }

    private fun onRoll() {
        if (gameOver || !playerTurn) return
        val d1 = rollDie(); val d2 = rollDie()
        setDice(d1,d2)
        val sum = d1 + d2
        tvRollSum.text = getString(R.string.roll_sum, sum)
        when {
            d1 == 1 && d2 == 1 -> { playerTotal = 0; turnScore = 0; endPlayerTurn() }
            d1 == 1 || d2 == 1 -> { turnScore = 0; endPlayerTurn() }
            else -> { turnScore += sum; updateTotals() }
        }
    }

    private fun onHold() {
        if (gameOver || !playerTurn) return
        playerTotal += turnScore; turnScore = 0; updateTotals()
        if (playerTotal >= 100) { gameOver = true; gamesWonPlayer++; showResult(true) }
        else endPlayerTurn()
    }

    private fun endPlayerTurn() {
        updateTotals(); playerTurn = false; updateHeader()
        btnRoll.isEnabled = false; btnHold.isEnabled = false
        turnScore = 0
        computerRolls = 0
        handler.postDelayed({ computerTurn() }, 1000)
    }

    private fun computerTurn() {
        if (gameOver) return

        val d1 = rollDie(); val d2 = rollDie()
        setDice(d1,d2)
        val sum = d1 + d2
        tvRollSum.text = getString(R.string.roll_sum, sum)
        updateTotals()

        when {
            d1 == 1 && d2 == 1 -> {
                computerTotal = 0
                turnScore = 0
                endComputerTurn(true)
            }
            d1 == 1 || d2 == 1 -> {
                turnScore = 0
                endComputerTurn(true)
            }
            else -> {
                turnScore += sum
                updateTotals()
                computerRolls++
                if (computerRolls < 3) {
                    handler.postDelayed({ computerTurn() }, 1000)
                } else {
                    endComputerTurn(false)
                }
            }
        }
    }

    private fun endComputerTurn(busted: Boolean) {
        if (!busted) {
            computerTotal += turnScore
        }
        turnScore = 0
        updateTotals()

        if (computerTotal >= 100) {
            gameOver = true
            gamesWonComputer++
            showResult(false)
        } else {
            playerTurn = true
            updateHeader()
            btnRoll.isEnabled = true
            btnHold.isEnabled = true
        }
    }

    private fun showResult(playerWon: Boolean) {
        imgResult.visibility = View.VISIBLE
        tvTapStart.visibility = View.VISIBLE
        imgResult.setImageResource(if (playerWon) R.drawable.winner else R.drawable.loser)
        updateHeader()

        // Build winner data for leaderboard
        val winnerName = if (playerWon) {
            getString(R.string.human)
        } else {
            getString(R.string.computer)
        }

        val winnerScore = if (playerWon) {
            playerTotal
        } else {
            computerTotal
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val intent = Intent(this, LeaderboardActivity::class.java).apply {
            putExtra(LeaderboardActivity.EXTRA_WINNER_NAME, winnerName)
            putExtra(LeaderboardActivity.EXTRA_WINNER_SCORE, winnerScore)
            putExtra(LeaderboardActivity.EXTRA_WINNER_DATE, dateStr)
        }
        startActivity(intent)
    }

    private fun updateHeader() {
        val who = if (playerTurn) getString(R.string.human) else getString(R.string.computer)
        tvWho.text = getString(R.string.who_turn, who)
        tvGamesWon.text = getString(R.string.games_won, gamesWonPlayer, gamesWonComputer)
    }

    private fun updateTotals() {
        val total = if (playerTurn) playerTotal else computerTotal
        tvTotal.text = getString(R.string.total_score, total)
        tvTurn.text = getString(R.string.turn_score, turnScore)
    }

    private fun setDice(d1: Int, d2: Int) {
        lastDie1 = d1
        lastDie2 = d2
        imgDie1.setImageResource(dieRes(d1))
        imgDie2.setImageResource(dieRes(d2))
    }

    private fun dieRes(n: Int): Int = when (n) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    private fun rollDie(): Int = Random.nextInt(1,7)
}

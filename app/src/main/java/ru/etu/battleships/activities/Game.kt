package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityGameBinding
import ru.etu.battleships.model.GameMode
import ru.etu.battleships.extUI.InfoGameDialog
import ru.etu.battleships.extUI.QuestionDialog
import ru.etu.battleships.model.Point
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    enum class Turn {
        LEFT_PLAYER,
        RIGHT_PLAYER,
        BOT,
    }

    private lateinit var binding: ActivityGameBinding
    private lateinit var questionDialog: QuestionDialog
    private lateinit var helpDialog: InfoGameDialog
    private var currentPlayer = Turn.LEFT_PLAYER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
        helpDialog = InfoGameDialog(this)
        setContentView(binding.root)

        val app = application as Application

        binding.apply {
            btBack.setOnClickListener {
                questionDialog.setMessage(resources.getString(R.string.back_dialog_message))
                questionDialog.setOnAcceptListener {
                    val intent = Intent(this@Game, Entry::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                questionDialog.show()
            }

            btExit.setOnClickListener {
                questionDialog.setMessage(resources.getString(R.string.exit_dialog_message))
                questionDialog.setOnAcceptListener {
                    finishAffinity()
                    exitProcess(0)
                }
                questionDialog.show()
            }

            btHelp.setOnClickListener {
                helpDialog.show()
            }

            leftPlayer.initGameField(app.player1.ships)
            val ai = when (app.gameMode) {
                GameMode.PVP -> rightPlayer.initGameField(app.player2.ships)
                GameMode.PVE -> rightPlayer.initGameField(app.player2.ships, isBot = true)
                else -> throw IllegalStateException()
            }
            rightPlayer.initGameField(app.player2.ships)

            Log.d("QWEQWE", app.player1.name)
            Log.d("QWEQWE", app.player2.name)

            leftPlayer.invalidate()
            rightPlayer.invalidate()

            leftPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "left player | (${point.x};${point.y})")
                if (app.gameMode == GameMode.PVP) {
                    if (currentPlayer == Turn.RIGHT_PLAYER) {
                        val (isKeep, _) = leftPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                        if (leftPlayer.gameModel!!.isOver()) {
                            Toast.makeText(
                                this@Game,
                                "Player 1 lost!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        currentPlayer = if (isKeep) {
                            Turn.RIGHT_PLAYER
                        } else {
                            playerTurnArrow.animate().rotation(0f).start()
                            Turn.LEFT_PLAYER
                        }
                    }
                }
            }

            rightPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "right player | (${point.x};${point.y})")
                if (currentPlayer == Turn.LEFT_PLAYER) {
                    val (isKeep, _) = rightPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                    if (rightPlayer.gameModel!!.isOver()) {
                        Toast.makeText(
                            this@Game,
                            "Player 2 lost!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    currentPlayer = if (isKeep) {
                        Turn.LEFT_PLAYER
                    } else {
                        playerTurnArrow.animate().rotation(180f).start()
//                        Turn.LEFT_PLAYER
                        when (app.gameMode) {
                            GameMode.PVP -> {
                                Turn.RIGHT_PLAYER
                            }
                            GameMode.PVE -> {
                                ai?.getPointToShot()?.let {
                                    val (isKeep, _) = leftPlayer.gameModel!!.hit(it.x, it.y)
                                }
                                Turn.LEFT_PLAYER
                            }
                            else -> throw IllegalStateException()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        questionDialog.setMessage(resources.getString(R.string.back_dialog_message))
        questionDialog.setOnAcceptListener {
            val intent = Intent(this@Game, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        questionDialog.show()
    }
}

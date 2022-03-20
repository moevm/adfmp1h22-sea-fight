package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityGameBinding
import ru.etu.battleships.extUI.InfoGameDialog
import ru.etu.battleships.extUI.QuestionDialog
import ru.etu.battleships.extUI.WinnerDialog
import ru.etu.battleships.model.CellState
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.UserScore
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var questionDialog: QuestionDialog
    private lateinit var helpDialog: InfoGameDialog
    private lateinit var winnerDialog: WinnerDialog

    private var currentPlayer = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
        helpDialog = InfoGameDialog(this)
        winnerDialog = WinnerDialog(this)
            .setOnBackListener { binding.btBack.performClick() }
            .setOnExitListener { binding.btExit.performClick() }
        setContentView(binding.root)

        val app = application as Application

        binding.apply {
            btBack.setOnClickListener {
                questionDialog.setMessage(resources.getString(R.string.back_dialog_message))
                questionDialog.setOnAcceptListener {
                    val intent = Intent(this@Game, Entry::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    winnerDialog.dismiss()
                    startActivity(intent)
                }
                questionDialog.show()
            }

            btExit.setOnClickListener {
                questionDialog.setMessage(resources.getString(R.string.exit_dialog_message))
                questionDialog.setOnAcceptListener {
                    winnerDialog.dismiss()
                    finishAffinity()
                    exitProcess(0)
                }
                questionDialog.show()
            }

            btHelp.setOnClickListener {
                helpDialog.show()
            }

            usernamePlayer1.text = app.player1.name
            usernamePlayer1.text = app.player2.name

            leftPlayer.initGameField(app.player1.ships)
            rightPlayer.initGameField(app.player2.ships)

            leftPlayer.invalidate()
            rightPlayer.invalidate()

            leftPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "left player | (${point.x};${point.y})")
                if (currentPlayer == 1) {
                    leftPlayer.hitCell(point)
                    val (isKeep, _) = leftPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                    if (leftPlayer.gameModel!!.isOver()) {
                        winnerDialog.setScore(
                            UserScore(usernamePlayer1.text.toString(), victoriesPlayer1.text.toString().toInt()),
                            UserScore(usernamePlayer2.text.toString(), victoriesPlayer2.text.toString().toInt())
                        ).setWinner(usernamePlayer2.text.toString()).show()
                    }
                    currentPlayer = if (isKeep) { 1 } else {
                        playerTurnArrow.animate().rotation(0f).start(); 2
                    }
                }
                leftPlayer.invalidate()
            }

            rightPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "right player | (${point.x};${point.y})")
                if (currentPlayer == 2) {
                    rightPlayer.hitCell(point)
                    val (isKeep, _) = rightPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                    if (rightPlayer.gameModel!!.isOver()) {
                        winnerDialog.setScore(
                            UserScore(usernamePlayer1.text.toString(), victoriesPlayer1.text.toString().toInt()),
                            UserScore(usernamePlayer2.text.toString(), victoriesPlayer2.text.toString().toInt())
                        ).setWinner(usernamePlayer1.text.toString()).show()
                    }
                    currentPlayer = if (isKeep) { 2 } else {
                        playerTurnArrow.animate().rotation(180f).start(); 1
                    }
                }
                rightPlayer.invalidate()
            }
        }
    }

    override fun onBackPressed() {
        binding.btBack.performClick()
    }
}

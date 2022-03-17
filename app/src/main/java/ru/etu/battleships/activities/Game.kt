package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityGameBinding
import ru.etu.battleships.databinding.DialogQuestionBinding
import ru.etu.battleships.model.Point


class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var currentPlayer = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as Application

        binding.apply {
            btBack.setOnClickListener {
                this@Game.openDialog(resources.getString(R.string.back_dialog_message)) {
                    val intent = Intent(this@Game, Entry::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }

            btExit.setOnClickListener {
                this@Game.openDialog(resources.getString(R.string.exit_dialog_message)) {
                    finishAffinity()
                    exitProcess(0)
                }
            }

            leftPlayer.initGameField(app.player1.ships)
            rightPlayer.initGameField(app.player2.ships)

            leftPlayer.invalidate()
            rightPlayer.invalidate()

            leftPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "left player | (${point.x};${point.y})")
                if (currentPlayer == 1) {
                    leftPlayer.hitCell(point)
                    val (isKeep, _) = leftPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                    currentPlayer = if (isKeep) {
                        1
                    } else {
                        playerTurnArrow.animate().rotation(0f).start()
                        2
                    }
                }
                leftPlayer.invalidate()
            }

            rightPlayer.setOnTapListener { point: Point ->
                Log.d("TAP", "right player | (${point.x};${point.y})")
                if (currentPlayer == 2) {
                    rightPlayer.hitCell(point)
                    val (isKeep, _) = rightPlayer.gameModel!!.hit(point.x - 1, point.y - 1)
                    currentPlayer = if (isKeep) {
                        2
                    } else {
                        playerTurnArrow.animate().rotation(180f).start()
                        1
                    }
                }
                rightPlayer.invalidate()
            }
        }

        Log.d("PLAYER", "player 1 name: ${app.player1.name}")
        Log.d("PLAYER", "player 2 name: ${app.player2.name}")
    }

    override fun onBackPressed() {
        this.openDialog(resources.getString(R.string.back_dialog_message)) {
            val intent = Intent(this@Game, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun openDialog(message: String, acceptListener: View.OnClickListener) {
        val viewBinding = DialogQuestionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(viewBinding.root)
            .create()

        viewBinding.message.text = message
        viewBinding.accept.setOnClickListener {
            alertDialog.dismiss()
            acceptListener.onClick(it)
        }
        viewBinding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}

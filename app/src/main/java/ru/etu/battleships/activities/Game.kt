package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.Application
import ru.etu.battleships.BuildConfig
import ru.etu.battleships.GameVibrator
import ru.etu.battleships.R
import ru.etu.battleships.SFXPlayer
import ru.etu.battleships.databinding.ActivityGameBinding
import ru.etu.battleships.db.UsersDBHelper
import ru.etu.battleships.extUI.InfoGameDialog
import ru.etu.battleships.extUI.QuestionDialog
import ru.etu.battleships.extUI.WinnerDialog
import ru.etu.battleships.model.AI
import ru.etu.battleships.model.GameMode
import ru.etu.battleships.model.PlayerStep
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Turn
import ru.etu.battleships.model.UserScore
import ru.etu.battleships.views.PlayingGameFieldView
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var questionDialog: QuestionDialog
    private lateinit var helpDialog: InfoGameDialog
    private lateinit var winnerDialog: WinnerDialog
    private lateinit var dbHelper: UsersDBHelper
    private lateinit var sfxPlayer: SFXPlayer

    private lateinit var vibrator: GameVibrator

    private val turnHistory = mutableListOf<PlayerStep>()
    private var currentPlayer = Turn.LEFT_PLAYER
    private var botTurnReactionTimeMs = 700L
    private var botHitReactionTimeMs = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        dbHelper = UsersDBHelper(this)
        vibrator = GameVibrator(this)
        sfxPlayer = SFXPlayer(this)
        questionDialog = QuestionDialog(this)
        helpDialog = InfoGameDialog(this)
        winnerDialog = WinnerDialog(this)
            .setOnBackListener { binding.btBack.performClick() }
            .setOnExitListener { binding.btExit.performClick() }
        setContentView(binding.root)

        val app = application as Application

        val leftPlayerScore = dbHelper.getWinnerLoserScore(app.player1.name, app.player2.name)
        val rightPlayerScore = dbHelper.getWinnerLoserScore(app.player2.name, app.player1.name)

        binding.apply {
            randomizeFirstPlayer(leftPlayer, rightPlayer, playerTurnArrow)
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
            usernamePlayer2.text = app.player2.name

            victoriesPlayer1.text = leftPlayerScore.toString()
            victoriesPlayer2.text = rightPlayerScore.toString()

            leftPlayer.initGameField(app.player1.ships)
            rightPlayer.initGameField(app.player2.ships)

            val ai = when (app.gameMode) {
                GameMode.PVP -> null
                GameMode.PVE -> AI(leftPlayer.gameModel!!)
                else -> throw IllegalStateException()
            }

            leftPlayer.invalidate()
            rightPlayer.invalidate()

            leftPlayer.setOnTapListener { point: Point ->
                if (app.gameMode == GameMode.PVP) {
                    if (currentPlayer == Turn.RIGHT_PLAYER) {
                        val previousCellState =
                            leftPlayer.gameModel!!.getCell(point.x - 1, point.y - 1)
                        val (isKeep, nextCellState) = leftPlayer.gameModel!!.hit(
                            point.x - 1,
                            point.y - 1
                        )
                        turnHistory.add(
                            PlayerStep(
                                currentPlayer,
                                point,
                                previousCellState,
                                nextCellState
                            )
                        )
                        if (leftPlayer.gameModel!!.isOver()) {
                            app.turnHistory = turnHistory
                            if (BuildConfig.DEBUG) {
                                turnHistory.forEach { turn ->
                                    Log.d(
                                        "turnHistory",
                                        "${turn.player} | ${turn.point} ${turn.previousCellState} -> ${turn.nextCellState}"
                                    )
                                }
                            }
                            dbHelper.addScoreForPair(
                                winner = usernamePlayer2.text.toString(),
                                loser = usernamePlayer1.text.toString(),
                            )
                            winnerDialog.setScore(
                                UserScore(
                                    usernamePlayer1.text.toString(),
                                    victoriesPlayer1.text.toString().toInt()
                                ),
                                UserScore(
                                    usernamePlayer2.text.toString(),
                                    victoriesPlayer2.text.toString().toInt() + 1
                                )
                            ).setWinner(usernamePlayer2.text.toString()).show()
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
                    val previousCellState =
                        rightPlayer.gameModel!!.getCell(point.x - 1, point.y - 1)
                    val (isKeep, nextCellState) = rightPlayer.gameModel!!.hit(
                        point.x - 1,
                        point.y - 1
                    )
                    turnHistory.add(
                        PlayerStep(
                            currentPlayer,
                            point,
                            previousCellState,
                            nextCellState
                        )
                    )
                    if (rightPlayer.gameModel!!.isOver()) {
                        app.turnHistory = turnHistory
                        if (BuildConfig.DEBUG) {
                            turnHistory.forEach { turn ->
                                Log.d(
                                    "turnHistory",
                                    "${turn.player} | ${turn.point} ${turn.previousCellState} -> ${turn.nextCellState}"
                                )
                            }
                        }
                        dbHelper.addScoreForPair(
                            winner = usernamePlayer1.text.toString(),
                            loser = usernamePlayer2.text.toString(),
                        )
                        winnerDialog.setScore(
                            UserScore(
                                usernamePlayer1.text.toString(),
                                victoriesPlayer1.text.toString().toInt() + 1
                            ),
                            UserScore(
                                usernamePlayer2.text.toString(),
                                victoriesPlayer2.text.toString().toInt()
                            )
                        ).setWinner(usernamePlayer1.text.toString()).show()
                    }
                    currentPlayer = if (isKeep) {
                        Turn.LEFT_PLAYER
                    } else {
                        playerTurnArrow.animate().rotation(180f).start()
                        Handler(Looper.getMainLooper()).postDelayed(
                            { ai?.hit() },
                            botTurnReactionTimeMs
                        )
                        Turn.RIGHT_PLAYER
                    }
                }
            }

            leftPlayer.gameModel?.addOnHit {
                vibrator.explosion()
                sfxPlayer.playExplosion()
                Handler(Looper.getMainLooper()).postDelayed(
                    { ai?.hit() },
                    botHitReactionTimeMs
                )
                currentPlayer = Turn.RIGHT_PLAYER
            }

            leftPlayer.gameModel?.addOnMiss {
                vibrator.splash()
                sfxPlayer.playSplash()
                playerTurnArrow.animate().rotation(0f).start()
                currentPlayer = Turn.LEFT_PLAYER
                leftPlayer.areCrossLinesShowed = false
                rightPlayer.areCrossLinesShowed = true
            }

            rightPlayer.gameModel?.addOnHit {
                vibrator.explosion()
                sfxPlayer.playExplosion()
            }

            rightPlayer.gameModel?.addOnMiss {
                vibrator.splash()
                sfxPlayer.playSplash()
                leftPlayer.areCrossLinesShowed = true
                rightPlayer.areCrossLinesShowed = false
            }
        }
    }

    private fun randomizeFirstPlayer(
        leftPlayer: PlayingGameFieldView,
        rightPlayer: PlayingGameFieldView,
        playerTurnArrow: ImageView
    ) {
        Random(Date().time)
        currentPlayer = Turn.values().random()
        when (currentPlayer) {
            Turn.LEFT_PLAYER -> {
                rightPlayer.areCrossLinesShowed = true
                playerTurnArrow.rotation = 0F
            }
            Turn.RIGHT_PLAYER -> {
                leftPlayer.areCrossLinesShowed = true
                playerTurnArrow.rotation = 180F
            }
        }
    }

    override fun onBackPressed() {
        binding.btBack.performClick()
    }
}

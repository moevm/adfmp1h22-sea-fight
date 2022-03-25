package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivitySetupLeftBinding
import ru.etu.battleships.extUI.InfoSetupDialog
import ru.etu.battleships.extUI.QuestionDialog
import ru.etu.battleships.model.GameMode
import ru.etu.battleships.views.SetupGameFieldView
import ru.etu.battleships.views.ShipView

class SetupLeft : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding
    private lateinit var questionDialog: QuestionDialog
    private lateinit var helpDialog: InfoSetupDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
            .setMessage(resources.getString(R.string.back_dialog_message))
            .setOnAcceptListener {
                val intent = Intent(this, Entry::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        helpDialog = InfoSetupDialog(this)

        binding.apply {
            btBack.setOnClickListener {
                questionDialog.show()
            }

            btHelp.setOnClickListener {
                helpDialog.show()
            }

            btNext.setOnClickListener {
                if (!gameFieldView.allShipsArePlaced()) {
                    toast(resources.getString(R.string.not_all_ships_are_placed))
                    return@setOnClickListener
                }

                val app = application as Application
                val playerName = etPlayerName.text.toString()
                    .ifEmpty { resources.getString(R.string.nickname_hint_1) }

                resources.getString(R.string.bot_name).let { botName ->
                    if (playerName == botName) {
                        toast(resources.getString(R.string.bot_name_conflict, botName))
                        return@setOnClickListener
                    }
                }

                app.setPlayer1State(
                    playerName,
                    gameFieldView.getShips()
                )
                val intent = when (app.gameMode) {
                    GameMode.PVP -> Intent(this@SetupLeft, SetupRight::class.java)
                    GameMode.PVE -> {
                        val ships = SetupGameFieldView.shuffleShips()
                        app.setPlayer2State(resources.getString(R.string.bot_name), ships)
                        Intent(this@SetupLeft, Game::class.java)
                    }
                    else -> throw IllegalStateException()
                }
                startActivity(intent)
            }

            gameFieldView.setupPullView(llTools)

            gameFieldView.addOnShipDragListener { ship, view ->
                gameFieldView.removeShip(ship)
                val shadowBuilder = ShipView.DragShadowBuilder(view as ShipView)
                view.startDragAndDrop(null, shadowBuilder, view, 0)
            }

            shuffleButton.setOnClickListener {
                gameFieldView.shuffleShips(
                    listOf(
                        ship41, ship31, ship32, ship21, ship22, ship23,
                        ship11, ship12, ship13, ship14
                    )
                )
            }
        }

        setContentView(binding.root)
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onBackPressed() {
        questionDialog.show()
    }
}

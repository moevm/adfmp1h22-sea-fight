package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivitySetupRightBinding
import ru.etu.battleships.databinding.DialogQuestionBinding
import ru.etu.battleships.views.ShipView

class SetupRight : AppCompatActivity() {
    private lateinit var binding: ActivitySetupRightBinding
    private val modals = mutableSetOf<AlertDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupRightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                this@SetupRight.openDialog(resources.getString(R.string.back_dialog_message))
            }

            btNext.setOnClickListener {
                val app = application as Application
                app.setPlayer2State(etPlayerName.text.toString().ifEmpty { resources.getString(R.string.nickname_hint_2) }, gameFieldView.getShips())

                val intent = Intent(this@SetupRight, Game::class.java)
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
    }

    override fun onBackPressed() {
        this.openDialog(resources.getString(R.string.back_dialog_message))
    }

    private fun openDialog(message: String) {
        val viewBinding = DialogQuestionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(viewBinding.root)
            .create()

        viewBinding.message.text = message
        viewBinding.accept.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        viewBinding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        modals.add(alertDialog)
        alertDialog.show()
    }
}

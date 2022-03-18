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
                if (gameFieldView.allShipsArePlaced()) {
                    val app = application as Application
                    app.setPlayer1State(etPlayerName.text.toString().ifEmpty { resources.getString(R.string.nickname_hint_1) }, gameFieldView.getShips())

                    val intent = Intent(this@SetupLeft, SetupRight::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@SetupLeft,
                        resources.getString(R.string.not_all_ships_are_placed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            gameFieldView.setupPullView(llTools)

            gameFieldView.addOnShipDragListener { ship, view ->
                gameFieldView.removeShip(ship)
                val shadowBuilder = ShipView.DragShadowBuilder(view as ShipView)
                view.startDragAndDrop(null, shadowBuilder, view, 0)
            }
        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        questionDialog.show()
    }
}

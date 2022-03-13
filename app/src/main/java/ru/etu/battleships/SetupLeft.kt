package ru.etu.battleships

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivitySetupLeftBinding
import ru.etu.battleships.databinding.DialogQuestionBinding

class SetupLeft : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                this@SetupLeft.openDialog(resources.getString(R.string.back_dialog_message))
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
                val (_, length, id) = view.resources.getResourceName(view.id)
                    .split("/")[1].split("_")
                val data = ClipData("Ship", arrayOf(GameFieldView.MIME_TYPE), ClipData.Item("${length}_$id"))
                val shadowBuilder = View.DragShadowBuilder(view)
                view.startDragAndDrop(data, shadowBuilder, view, 0)
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

        alertDialog.show()
    }
}

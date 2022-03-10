package ru.etu.battleships

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import ru.etu.battleships.databinding.ActivitySetupLeftBinding
import ru.etu.battleships.databinding.DialogQuestionBinding

class SetupLeft : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding

    private val TAG = "DEBUG_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                this@SetupLeft.openDialog(resources.getString(R.string.back_dialog_message))
            }

            rotateButton.setOnClickListener {
                gameFieldView.rotateLastShip()
                gameFieldView.invalidate()
            }

            btNext.setOnClickListener {
                if (gameFieldView.allShipsArePlaced()) {
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

            gameFieldView.setOnDragListener(dragListener)

            val ships = mapOf(
                ship41 to "4_1",

                ship31 to "3_1",
                ship32 to "3_2",

                ship21 to "2_1",
                ship22 to "2_2",
                ship23 to "2_3",

                ship11 to "1_1",
                ship12 to "1_2",
                ship13 to "1_3",
                ship14 to "1_4",
            )

            // TODO: fix linter
            ships.forEach { (ship, mineType) ->
                ship.setOnTouchListener { view, _ ->
                    val data = ClipData.newPlainText("Ship", mineType)
                    val shadowBuilder = View.DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                }
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
            val intent = Intent(this, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        viewBinding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private val dragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text

                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                val destination = view as GameFieldView
                val hasPlaced = destination.addShip(dragData, event.x, event.y, owner, v)
                v.visibility = View.VISIBLE
                hasPlaced
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }
}

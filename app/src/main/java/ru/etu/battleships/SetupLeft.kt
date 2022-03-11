package ru.etu.battleships

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import ru.etu.battleships.databinding.ActivitySetupLeftBinding
import ru.etu.battleships.databinding.DialogQuestionBinding

class SetupLeft : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding

    private val TAG = "DEBUG_TAG"

    private lateinit var _gameFieldView: GameFieldView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            clTools.setOnDragListener(dragListener)

            _gameFieldView = gameFieldView

            clTools.forEach { linearLayout ->
                if (linearLayout !is LinearLayout) {
                    return@forEach
                }
                linearLayout.forEach { ship ->
                    ship.setOnTouchListener { view, _ ->
                        gameFieldView.setSelectedShipToNull()
                        val (_, length, id) = view.resources.getResourceName(view.id)
                            .split("/")[1].split("_")
                        val data = ClipData.newPlainText("Ship", "${length}_$id")
                        val shadowBuilder = View.DragShadowBuilder(view)
                        view.startDragAndDrop(data, shadowBuilder, view, 0)
                    }
                }
            }

            btBack.setOnClickListener {
                this@SetupLeft.openDialog(resources.getString(R.string.back_dialog_message))
            }

            rotateButton.setOnClickListener {
//                gameFieldView.rotateLastShip()
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

            gameFieldView.setOnShipDrag { view: View ->
                Log.d("setOnShipDrag lambda", "qweqwe")
                val (_, length, id) = view.resources.getResourceName(view.id)
                    .split("/")[1].split("_")
                val data = ClipData.newPlainText("Ship", "${length}_$id")
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
                val v = event.localState as View
                v.visibility = View.VISIBLE
                view.invalidate()
                _gameFieldView.selectedShipOut()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }
}

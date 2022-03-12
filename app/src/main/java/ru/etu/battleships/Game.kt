package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivityGameBinding
import ru.etu.battleships.databinding.DialogQuestionBinding
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        }
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

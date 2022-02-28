package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivityEntryBinding
import ru.etu.battleships.databinding.DialogQuestionBinding
import kotlin.system.exitProcess

class Entry : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            pvpButton.setOnClickListener {
                val intent = Intent(this@Entry, SetupLeft::class.java)
                startActivity(intent)
            }
            pveButton.setOnClickListener {
                val intent = Intent(this@Entry, SetupLeft::class.java)
                startActivity(intent)
            }
            settingsButton.setOnClickListener {
                val intent = Intent(this@Entry, Menu::class.java)
                startActivity(intent)
            }
            btScoreboard.setOnClickListener {
                val intent = Intent(this@Entry, ScoreBoard::class.java)
                startActivity(intent)
            }
            exitButton.setOnClickListener {
                this@Entry.openDialog(resources.getString(R.string.exit_dialog_message))
            }
        }
    }

    override fun onBackPressed() {
        this.openDialog(resources.getString(R.string.exit_dialog_message))
    }

    private fun openDialog(message: String) {
        val viewBinding = DialogQuestionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(viewBinding.root)
            .create()

        viewBinding.message.text = message
        viewBinding.accept.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
        viewBinding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}

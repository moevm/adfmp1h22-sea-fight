package ru.etu.battleships.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityEntryBinding
import ru.etu.battleships.extUI.QuestionDialog
import kotlin.system.exitProcess

class Entry : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var questionDialog: QuestionDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
            .setMessage(resources.getString(R.string.exit_dialog_message))
            .setOnAcceptListener {
                finishAffinity()
                exitProcess(0)
            }

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
                val intent = Intent(this@Entry, Preferences::class.java)
                startActivity(intent)
            }
            btScoreboard.setOnClickListener {
                val intent = Intent(this@Entry, ScoreBoard::class.java)
                startActivity(intent)
            }
            exitButton.setOnClickListener {
                questionDialog.show()
            }
        }

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        questionDialog.show()
    }
}

package ru.etu.battleships.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityMenuBinding
import ru.etu.battleships.extUI.QuestionDialog
import kotlin.system.exitProcess

class Menu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var questionDialog: QuestionDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
            .setMessage(resources.getString(R.string.exit_dialog_message))
            .setOnAcceptListener {
                finishAffinity()
                exitProcess(0)
            }

        binding.apply {
            back.setOnClickListener {
                finish()
            }

            exitButton.setOnClickListener {
                questionDialog.show()
            }
        }

        setContentView(binding.root)
    }
}

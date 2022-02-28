package ru.etu.battleships

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.etu.battleships.databinding.ActivityScoreBoardBinding
import ru.etu.battleships.databinding.DialogQuestionBinding
import kotlin.system.exitProcess

class ScoreBoard : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBoardBinding
    private val adapter = ScoreboardAdapter()

    /* TODO: hardcoded user score */
    private val users = listOf(
        UserScore("player1", 7),
        UserScore("player2", 2),
        UserScore("Anya", 8),
        UserScore("Constantine", 6),
        UserScore("Kirill", 9),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.apply {
            back.setOnClickListener {
                finish()
            }
            exitButton.setOnClickListener {
                this@ScoreBoard.openDialog(resources.getString(R.string.exit_dialog_message))
            }

            scoreboardTable.layoutManager = LinearLayoutManager(this@ScoreBoard)
            scoreboardTable.adapter = adapter
            adapter.setUserScoreList(users)
        }
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

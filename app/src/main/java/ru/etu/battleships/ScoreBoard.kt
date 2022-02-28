package ru.etu.battleships

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.etu.battleships.databinding.ActivityScoreBoardBinding
import kotlin.system.exitProcess


class ScoreBoard : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBoardBinding
    private val adapter = ScoreboardAdapter()

    /* TODO: hardcoded user score */
    private var users = mutableListOf(
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
                finishAffinity()
                exitProcess(0)
            }

            scoreboardTable.layoutManager = LinearLayoutManager(this@ScoreBoard)
            scoreboardTable.adapter = adapter
            adapter.setUserScoreList(users)
        }
    }
}

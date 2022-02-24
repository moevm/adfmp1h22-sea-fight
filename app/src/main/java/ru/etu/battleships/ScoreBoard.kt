package ru.etu.battleships

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import kotlin.system.exitProcess

class ScoreBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_board)

        val back: ImageButton = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val exit: ImageButton = findViewById(R.id.exitButton)
        exit.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
    }
}
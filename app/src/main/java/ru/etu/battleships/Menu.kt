package ru.etu.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import kotlin.system.exitProcess


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

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
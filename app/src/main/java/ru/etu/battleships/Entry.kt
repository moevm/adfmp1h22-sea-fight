package ru.etu.battleships

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import ru.etu.battleships.databinding.ActivityEntryBinding
import kotlin.system.exitProcess

class Entry : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settings: ImageButton = findViewById(R.id.settingsButton)
        settings.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        val exit: ImageButton = findViewById(R.id.exitButton)
        exit.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
    }
}
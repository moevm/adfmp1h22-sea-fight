package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivityEntryBinding
import kotlin.system.exitProcess

class Entry : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var entry: Entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        entry = this


        binding.apply {
            pvpButton.setOnClickListener {
                val intent = Intent(entry, SetupLeft::class.java)
                startActivity(intent)
            }
            pveButton.setOnClickListener {
                val intent = Intent(entry, SetupLeft::class.java)
                startActivity(intent)
            }
            settingsButton.setOnClickListener {
                val intent = Intent(entry, Menu::class.java)
                startActivity(intent)
            }
            exitButton.setOnClickListener {
                finishAffinity()
                exitProcess(0)
            }
        }
    }
}
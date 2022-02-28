package ru.etu.battleships

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivityGameBinding
import kotlin.system.exitProcess

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                finish()
            }
            btExit.setOnClickListener {
                finishAffinity()
                exitProcess(0)
            }
        }
    }
}
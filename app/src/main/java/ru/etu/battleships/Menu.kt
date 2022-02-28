package ru.etu.battleships

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivityMenuBinding
import kotlin.system.exitProcess


class Menu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            back.setOnClickListener {
                finish()
            }

            exitButton.setOnClickListener {
                finishAffinity()
                exitProcess(0)
            }
        }
    }
}

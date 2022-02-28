package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivitySetupLeftBinding

class SetupRight : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding
    private lateinit var setupRight: SetupRight

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRight = this

        binding.apply {
            btBack.setOnClickListener {
                finish()
            }

            btNext.setOnClickListener {
                val intent = Intent(setupRight, Game::class.java)
                startActivity(intent)
            }
        }
    }
}
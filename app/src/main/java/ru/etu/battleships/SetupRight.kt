package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivitySetupLeftBinding

class SetupRight : AppCompatActivity() {
    private lateinit var binding: ActivitySetupLeftBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupLeftBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                val intent = Intent(this@SetupRight, Entry::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                startActivity(intent)
            }

            btNext.setOnClickListener {
                val intent = Intent(this@SetupRight, Game::class.java)
                startActivity(intent)
            }
        }
    }
}

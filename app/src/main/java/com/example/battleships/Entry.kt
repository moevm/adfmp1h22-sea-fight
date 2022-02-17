package com.example.battleships

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.battleships.databinding.ActivityEntryBinding

class Entry : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println(binding.exitButton.width)
    }
}
package ru.etu.battleships

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.etu.battleships.databinding.ActivitySetupRightBinding
import ru.etu.battleships.databinding.DialogQuestionBinding

class SetupRight : AppCompatActivity() {
    private lateinit var binding: ActivitySetupRightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupRightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btBack.setOnClickListener {
                this@SetupRight.openDialog(resources.getString(R.string.back_dialog_message))
            }

            btNext.setOnClickListener {
                val intent = Intent(this@SetupRight, Game::class.java)
                startActivity(intent)
            }
        }
    }

    private fun openDialog(message: String) {
        val viewBinding = DialogQuestionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(viewBinding.root)
            .create()

        viewBinding.message.text = message
        viewBinding.accept.setOnClickListener {
            val intent = Intent(this, Entry::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        viewBinding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}

package ru.etu.battleships.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import ru.etu.battleships.Application
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityPreferencesBinding
import ru.etu.battleships.extUI.QuestionDialog
import kotlin.system.exitProcess

class Preferences : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding
    private lateinit var questionDialog: QuestionDialog

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
            .setMessage(resources.getString(R.string.exit_dialog_message))
            .setOnAcceptListener {
                finishAffinity()
                exitProcess(0)
            }

        prefs = getSharedPreferences(Application.APP_NAME, MODE_PRIVATE)

        binding.apply {
            back.setOnClickListener {
                finish()
            }

            exitButton.setOnClickListener {
                questionDialog.show()
            }

            sound.isChecked = prefs.getBoolean(Application.APP_SOUNDS_PREFERENCE, true)
            sound.setOnCheckedChangeListener { _, b: Boolean ->
                prefs.edit {
                    putBoolean(Application.APP_SOUNDS_PREFERENCE, b)
                    apply()
                }
            }

            vibration.isChecked = prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)
            vibration.setOnCheckedChangeListener { _, b: Boolean ->
                prefs.edit {
                    putBoolean(Application.APP_VIBRATION_PREFERENCE, b)
                    apply()
                }
            }
        }

        setContentView(binding.root)
    }
}

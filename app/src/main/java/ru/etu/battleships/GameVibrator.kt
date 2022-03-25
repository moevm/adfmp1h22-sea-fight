package ru.etu.battleships

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class GameVibrator(context: Context) {
    private val prefs = context.getSharedPreferences(Application.APP_NAME, Context.MODE_PRIVATE)

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrationManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrationManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val attrs: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val clickEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    private val splashEffect: VibrationEffect
    private val explosionEffect: VibrationEffect

    init {
        val splashAmplitudePattern = intArrayOf(68, 49, 76, 22, 127, 93, 35, 20, 12, 9, 6, 5, 5, 9, 9)
        val splashTimesPattern = LongArray(splashAmplitudePattern.size) { 1000L / splashAmplitudePattern.size }
        splashEffect = VibrationEffect.createWaveform(splashTimesPattern, splashAmplitudePattern, -1)

        val explosionAmplitudePattern = intArrayOf(79, 255, 200, 127, 174, 144, 155, 118, 97, 72, 51, 43, 46, 44, 45, 37, 31, 27, 27, 19)
        val explosionTimePattern = LongArray(explosionAmplitudePattern.size) {2000L / explosionAmplitudePattern.size}
        explosionEffect = VibrationEffect.createWaveform(explosionTimePattern, explosionAmplitudePattern, -1)
    }

    fun click() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            vibrator.vibrate(clickEffect, attrs)
        }
    }

    fun explosion() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            vibrator.vibrate(explosionEffect, attrs)
        }
    }

    fun splash() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            vibrator.vibrate(splashEffect, attrs)
        }
    }
}
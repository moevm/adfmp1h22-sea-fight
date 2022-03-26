package ru.etu.battleships

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.lang.Integer.max

class GameVibrator(context: Context) {
    companion object {
        val explosionAmps = doubleArrayOf(0.313, 1.0, 0.786, 0.499, 0.684, 0.567, 0.609, 0.464, 0.381, 0.284, 0.201, 0.169, 0.184, 0.174, 0.179, 0.147, 0.125, 0.108, 0.109, 0.077)
        const val explosionMax = 255

        val splashAmps = doubleArrayOf(0.539, 0.388, 0.597, 0.174, 1.0, 0.736, 0.278, 0.162, 0.096, 0.072, 0.049, 0.043, 0.046, 0.072, 0.072, 0.052, 0.043, 0.067, 0.035, 0.012)
        const val splashMax = 100
    }

    private val prefs = context.getSharedPreferences(Application.APP_NAME, Context.MODE_PRIVATE)

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrationManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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
        // ho-ho, magic numbers
        val splashAmplitudePattern = splashAmps.map { (it * splashMax).toInt() }.toIntArray()
        val splashTimesPattern =
            LongArray(splashAmplitudePattern.size) { 500L / max(splashAmplitudePattern.size, 20) }

        splashEffect =
            VibrationEffect.createWaveform(splashTimesPattern, splashAmplitudePattern, -1)

        // ho-ho, magic numbers
        val explosionAmplitudePattern = explosionAmps.map { (it * explosionMax).toInt() }.toIntArray()
        val explosionTimePattern =
            LongArray(explosionAmplitudePattern.size) { 1000L / max(explosionAmplitudePattern.size, 20) }

        explosionEffect =
            VibrationEffect.createWaveform(explosionTimePattern, explosionAmplitudePattern, -1)
    }

    fun click() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            vibrator.vibrate(clickEffect, attrs)
        }
    }

    fun explosion() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(explosionEffect, attrs)
            } else {
                vibrator.vibrate(1000);
            }
        }
    }

    fun splash() {
        if (prefs.getBoolean(Application.APP_VIBRATION_PREFERENCE, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(splashEffect, attrs)
            } else {
                vibrator.vibrate(500);
            }
        }
    }
}

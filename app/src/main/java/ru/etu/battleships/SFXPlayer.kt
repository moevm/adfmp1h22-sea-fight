package ru.etu.battleships

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SFXPlayer(context: Context) {
    private val prefs = context.getSharedPreferences(Application.APP_NAME, Context.MODE_PRIVATE)
    private val soundPool: SoundPool

    private val splash: Int
    private val explosion: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool =  SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        splash = soundPool.load(context, R.raw.sfx_splash, 1);
        explosion = soundPool.load(context, R.raw.sfx_explosion, 1);
    }

    fun playSplash() {
        if (prefs.getBoolean(Application.APP_SOUNDS_PREFERENCE, true)) {
            soundPool.play(splash, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playExplosion() {
        if (prefs.getBoolean(Application.APP_SOUNDS_PREFERENCE, true)) {
            soundPool.play(explosion, 1f, 1f, 1, 0, 1f)
        }
    }
}

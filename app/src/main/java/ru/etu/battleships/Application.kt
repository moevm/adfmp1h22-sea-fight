package ru.etu.battleships

import android.app.Application as BaseApplication

class Application: BaseApplication() {
    private var player1Name: String = ""
    private var player2Name: String = ""

    override fun onCreate() {
        super.onCreate()

        player1Name = resources.getString(R.string.nickname_hint)
        player2Name = resources.getString(R.string.nickname_hint)
    }
}
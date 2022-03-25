package ru.etu.battleships

import ru.etu.battleships.model.GameMode
import ru.etu.battleships.model.Player
import ru.etu.battleships.model.PlayerStep
import ru.etu.battleships.model.Ship
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    companion object {
        const val APP_NAME = "Battleship"
        const val APP_SOUNDS_PREFERENCE = "sound_preference"
        const val APP_VIBRATION_PREFERENCE = "vibration_preference"
    }

    var player1 = Player()
        get() = Player(field)
    var player2 = Player()
        get() = Player(field)

    var turnHistory = mutableListOf<PlayerStep>()
        get() = field
        set(value) {
            field = value
        }

    var gameMode = GameMode.PREPARE
        get() = field
        set(value) {
            field = value
        }

    fun setPlayer1State(username: String, ships: Set<Ship>) {
        player1 = Player(username, ships)
    }

    fun setPlayer2State(username: String, ships: Set<Ship>) {
        player2 = Player(username, ships)
    }
}

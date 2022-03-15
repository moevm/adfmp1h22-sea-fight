package ru.etu.battleships

import ru.etu.battleships.model.Player
import ru.etu.battleships.model.Ship
import android.app.Application as BaseApplication

class Application : BaseApplication() {
    var player1 = Player()
        get() = Player(field)
    var player2 = Player()
        get() = Player(field)

    fun setPlayer1State(username: String, ships: Set<Ship>) {
        player1 = Player(username, ships)
    }

    fun setPlayer2State(username: String, ships: Set<Ship>) {
        player2 = Player(username, ships)
    }
}

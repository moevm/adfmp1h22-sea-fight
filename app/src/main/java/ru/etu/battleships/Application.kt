package ru.etu.battleships

import android.util.Log
import android.app.Application as BaseApplication

class Application: BaseApplication() {
    class Player(val name: String = "", val ships: Set<Ship> = emptySet()) {
        constructor(player: Player): this(player.name, player.ships)
    }

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
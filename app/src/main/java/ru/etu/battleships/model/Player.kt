package ru.etu.battleships.model


data class Player(val name: String = "", val ships: Set<Ship> = emptySet()) {
    constructor(player: Player) : this(player.name, player.ships)
}

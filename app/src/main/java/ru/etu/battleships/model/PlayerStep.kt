package ru.etu.battleships.model

data class PlayerStep(
    val player: String,
    val point: Point,
    val cellState: CellState
)

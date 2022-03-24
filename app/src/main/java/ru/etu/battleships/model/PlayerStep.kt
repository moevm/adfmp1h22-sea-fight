package ru.etu.battleships.model

data class PlayerStep(
    val player: Turn,
    val point: Point,
    val previousCellState: CellState,
    val nextCellState: CellState,
)

package ru.etu.battleships.model

class Ship(
    val length: Int,
    var position: Point,
    var orientation: Orientation,
    val id: Int,
) {
    constructor(other: Ship) : this(
        other.length,
        other.position,
        other.orientation,
        other.id
    ) {}

    fun set(other: Ship) {
        position = other.position
        orientation = other.orientation
    }

    fun rotate() {
        this.orientation = when (orientation) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }
    }
}

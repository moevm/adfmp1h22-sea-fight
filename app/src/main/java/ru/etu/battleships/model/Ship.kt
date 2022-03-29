package ru.etu.battleships.model

class Ship(
    var length: Int,
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
        length = other.length
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

package ru.etu.battleships.model

data class Ship(
    val length: Int,
    val position: Point,
    var orientation: Orientation,
    val id: Int,
) {
    fun rotate() {
        this.orientation = when (orientation) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }
    }
}

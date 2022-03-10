package ru.etu.battleships

data class Point(
    val x: Int,
    val y: Int,
)

data class Ship(
    val length: Int,
    val position: Point,
    var orientation: Orientation,
) {
    fun rotate() {
        this.orientation = when (orientation) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }
    }
}

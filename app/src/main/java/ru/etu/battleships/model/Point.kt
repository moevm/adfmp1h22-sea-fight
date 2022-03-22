package ru.etu.battleships.model

data class Point(
    val x: Int,
    val y: Int,
) {
    operator fun plus(otherPoint: Point) = Point(x + otherPoint.x, y + otherPoint.y)
}

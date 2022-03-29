package ru.etu.battleships.model

data class Point(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    override fun toString() = "($x;$y)"
}

package ru.etu.battleships.model

enum class Orientation {
    HORIZONTAL,
    VERTICAL;

    fun next(): Orientation {
        return when (this) {
            HORIZONTAL -> VERTICAL
            VERTICAL -> HORIZONTAL
        }
    }
}

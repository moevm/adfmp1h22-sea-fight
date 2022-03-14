package ru.etu.battleships

enum class CellState(val value: Int) {
    FREE(0),
    OCCUPIED(1),
    HIT(2),
    KILLED(3),
    MISS(4);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }

    fun isEndCell() = this == FREE || this == MISS
}

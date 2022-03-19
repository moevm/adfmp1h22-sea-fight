package ru.etu.battleships.model

class AI(private var gameModel: GameModel) {
    private val size = 10

    private val enemyShips = mutableListOf(
        1, 1, 1, 1,
        2, 2, 2,
        3, 3,
        4,
    )

    private var weights = MutableList(size) { MutableList(size) { 1 } }

    private val availableCells =
        (0 until size).flatMap { i -> (0 until size).map { j -> Point(i, j) } }.toMutableSet()


    fun getPointToShot(): Point {
        recalculateWeightMap()
        val maxWeight = weights.flatten().maxOrNull()
        val index = weights.flatten().indexOf(maxWeight)
        val y = index / size
        val x = index % size
        val point = Point(x, y)
        gameModel.hit(point.x, point.y)
        return point
    }

    // TODO: remove cells after destroying ship
    private fun removeCells(ship: Ship) {
        enemyShips.remove(ship.length)
        val shipCells = (0 until ship.length).map {
            ship.position + when (ship.orientation) {
                Orientation.VERTICAL -> Point(0, it)
                Orientation.HORIZONTAL -> Point(it, 0)
            }
        }

        val cells = shipCells.flatMap {
            (-1..1).flatMap { i -> (-1..1).map { j -> it + Point(i, j) } }
                .filter { it.x in (0 until size) && it.y in (0 until size) }
        }

        availableCells.removeAll(cells)
    }

    private fun recalculateWeightMap() {
        // TODO: implement game level
        weights = MutableList(size) { MutableList(size) { 1 } }
        repeat(size) { x ->
            repeat(size) { y ->
                if (gameModel.getCell(x, y) in listOf(
                        CellState.MISS,
                        CellState.KILLED,
                        CellState.HIT
                    )
                ) {
                    weights[y][x] = 0
                }

                if (gameModel.getCell(x, y) == CellState.HIT) {
                    if (x - 1 >= 0) {
                        if (y - 1 >= 0) {
                            weights[y - 1][x - 1] = 0
                        }
                        weights[y][x - 1] *= 50
                        if (y + 1 < size) {
                            weights[y + 1][x - 1] = 0
                        }
                    }
                    if (y - 1 >= 0) {
                        weights[y - 1][x] *= 50
                    }
                    if (y + 1 < size) {
                        weights[y + 1][x] *= 50
                    }
                    if (x + 1 < size) {
                        if (y - 1 >= 0) {
                            weights[y - 1][x + 1] = 0
                        }
                        weights[y][x + 1] *= 50
                        if (y + 1 < size) {
                            weights[y + 1][x + 1] = 0
                        }
                    }
                }
            }
        }
    }
}

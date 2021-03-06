package ru.etu.battleships.model

import java.lang.IllegalStateException

class GameModel(listOfShips: Set<Ship>) {
    private val matrix: MutableList<MutableList<CellState>> =
        MutableList(10) { MutableList(10) { CellState.FREE } }

    private val onShipKillCallbacks: MutableList<(Ship) -> Unit> = mutableListOf()

    private val onHitCallbacks: MutableList<(Point) -> Unit> = mutableListOf()
    private val onMissCallbacks: MutableList<(Point) -> Unit> = mutableListOf()
    private val onCellChangeCallbacks: MutableList<(Point, CellState, CellState) -> Unit> = mutableListOf()

    init {
        listOfShips.forEach { ship ->
            val pos = ship.position
            repeat(ship.length) {
                when (ship.orientation) {
                    Orientation.HORIZONTAL -> setCell(pos.x + it - 1, pos.y - 1, CellState.OCCUPIED)
                    Orientation.VERTICAL -> setCell(pos.x - 1, pos.y + it - 1, CellState.OCCUPIED)
                }
            }
        }
    }

    fun setMatrix(matrix: List<List<Int>>) {
        repeat(10) { i ->
            repeat(10) { j ->
                this.matrix[i][j] = CellState.fromInt(matrix[i][j])
            }
        }
    }

    fun getMatrix() = matrix.map { it.map { el -> el.value } }

    fun getCell(point: Point) = getCell(point.x, point.y)

    fun getCell(x: Int, y: Int): CellState {
        if (!(x in 0 until 10 && y in 0 until 10)) {
            return CellState.FREE
        }
        return matrix[y][x]
    }

    private fun setCell(point: Point, state: CellState) = setCell(point.x, point.y, state)

    private fun setCell(x: Int, y: Int, state: CellState) {
        if (!(x in 0 until 10 && y in 0 until 10)) {
            return
        }

        if (matrix[y][x] != state) {
            onCellChangeCallbacks.forEach { it(Point(x, y), matrix[y][x], state) }
        }

        matrix[y][x] = state
    }

    private fun check(x: Int, y: Int, isAdd: Boolean, isRow: Boolean): Int {
        var length = 0
        (1 until 4).forEach { i ->
            val cell = if (isAdd) {
                if (isRow) {
                    getCell(x + i, y)
                } else {
                    getCell(x, y + i)
                }
            } else {
                if (isRow) {
                    getCell(x - i, y)
                } else {
                    getCell(x, y - i)
                }
            }
            if (cell.isEndCell()) {
                return length
            }

            when (cell) {
                CellState.OCCUPIED -> return -1
                CellState.HIT -> length++
                else -> throw IllegalStateException()
            }
        }
        return length
    }

    fun isOver(): Boolean = matrix.flatMap { squashedMatrix ->
        squashedMatrix.filter { it == CellState.KILLED }
    }.size == (4 * 1 + 3 * 2 + 2 * 3 + 1 * 4)

    fun hit(x: Int, y: Int): Pair<Boolean, CellState> {
        val cell = getCell(x, y)
        if (
            cell == CellState.HIT ||
            cell == CellState.KILLED ||
            cell == CellState.MISS
        ) {
            return Pair(true, getCell(x, y))
        }

        if (cell == CellState.FREE) {
            setCell(x, y, CellState.MISS)
            onMissCallbacks.forEach { f -> f(Point(x, y)) }
            return Pair(false, getCell(x, y))
        }

        if (cell == CellState.OCCUPIED) {
            setCell(x, y, CellState.HIT)
            onHitCallbacks.forEach { f -> f(Point(x, y)) }
            var killed = false
            var vertical = false
            var length: Int
            if (
                getCell(x, y + 1).isEndCell() &&
                getCell(x, y - 1).isEndCell() &&
                getCell(x - 1, y).isEndCell() &&
                getCell(x + 1, y).isEndCell()
            ) {
                length = 1
                killed = true
            } else {
                vertical = false
                var tempKilled = true
                length = 1
                var additionalLength = check(x, y, isAdd = true, isRow = true)
                if (additionalLength > -1) {
                    length += additionalLength
                } else {
                    tempKilled = false
                }
                additionalLength = check(x, y, isAdd = false, isRow = true)
                if (additionalLength > -1) {
                    length += additionalLength
                } else {
                    tempKilled = false
                }
                if (length > 1) {
                    killed = tempKilled
                }
                if (length == 1) {
                    vertical = true
                    length = 1
                    additionalLength = check(x, y, isAdd = true, isRow = false)
                    if (additionalLength > -1) {
                        length += additionalLength
                    } else {
                        tempKilled = false
                    }
                    additionalLength = check(x, y, isAdd = false, isRow = false)
                    if (additionalLength > -1) {
                        length += additionalLength
                    } else {
                        tempKilled = false
                    }
                    if (length > 1) {
                        killed = tempKilled
                    }
                }
            }
            if (killed) {
                val point = findLeftShipCorner(x, y, vertical)
                hitAroundCells(point, length, vertical)
                val orientation = if (vertical) {
                    Orientation.VERTICAL
                } else {
                    Orientation.HORIZONTAL
                }
                onShipKillCallbacks.forEach { callback ->
                    callback(Ship(length, Point(point.x + 1, point.y + 1), orientation, -1))
                }
//                println(matrix)
            }
            return Pair(true, getCell(x, y))
        }
        throw IllegalStateException()
    }

    private fun hitAroundCells(point: Point, length: Int, vertical: Boolean) {
        if (vertical) {
//            ((point.x - 1)..(point.x + 1)).forEach { x ->
//                ((point.y - 1)..(point.y + length)).forEach { y ->
//                    setCell(x, y, CellState.MISS)
//                }
//            }
            // hit back and front of ship
            ((point.x - 1)..(point.x + 1)).forEach { x ->
                setCell(x, point.y - 1, CellState.MISS)
                setCell(x, point.y + length, CellState.MISS)
            }
            // hit along ship
            (point.y until (point.y + length)).forEach { y ->
                setCell(point.x, y, CellState.KILLED)
                setCell(point.x - 1, y, CellState.MISS)
                setCell(point.x + 1, y, CellState.MISS)
            }
        } else {
//            ((point.x - 1)..(point.x + length)).forEach { x ->
//                ((point.y - 1)..(point.y + 1)).forEach { y ->
//                    hit(x, y)
//                }
//            }
            // hit back and front of ship
            ((point.y - 1)..(point.y + 1)).forEach { y ->
                setCell(point.x - 1, y, CellState.MISS)
                setCell(point.x + length, y, CellState.MISS)
            }
            // hit along ship
            (point.x until (point.x + length)).forEach { x ->
                setCell(x, point.y, CellState.KILLED)
                setCell(x, point.y - 1, CellState.MISS)
                setCell(x, point.y + 1, CellState.MISS)
            }
        }
    }

    private fun findLeftShipCorner(x: Int, y: Int, vertical: Boolean): Point {
        repeat(5) { i ->
            if (vertical) {
                val cell = getCell(x, y - i)
                if (cell.isEndCell()) {
                    return Point(x, y - i + 1)
                }
            } else {
                val cell = getCell(x - i, y)
                if (cell.isEndCell()) {
                    return Point(x - i + 1, y)
                }
            }
        }
        throw IllegalStateException()
    }

    fun addOnKill(function: (Ship) -> Unit) {
        onShipKillCallbacks.add(function)
    }

    fun addOnHit(function: (Point) -> Unit) {
        onHitCallbacks.add(function)
    }

    fun addOnMiss(function: (Point) -> Unit) {
        onMissCallbacks.add(function)
    }

    fun addOnCellChange(function: (Point, prev: CellState, next: CellState) -> Unit) {
        onCellChangeCallbacks.add(function)
    }
}

package ru.etu.battleships

import junit.framework.TestCase
import org.junit.Assert

class GameModelTest : TestCase() {
    private val initField: List<List<Int>> = listOf(
        listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1),
        listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
        listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    fun testKillLongShip() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(3, 2)
        model.hit(1, 2)
        model.hit(0, 2)
        model.hit(2, 2)
        Assert.assertEquals(
            listOf(
                listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 4, 4, 4, 1, 0, 1, 0, 1),
                listOf(4, 3, 3, 3, 4, 1, 0, 1, 0, 0),
                listOf(4, 4, 4, 4, 4, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }

    fun testKeepPlayerAndReturnState() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        Assert.assertEquals(Pair(true, CellState.HIT), model.hit(0, 0))
        Assert.assertEquals(Pair(true, CellState.HIT), model.hit(0, 0))
        Assert.assertEquals(Pair(false, CellState.MISS), model.hit(1, 1))
        Assert.assertEquals(Pair(true, CellState.MISS), model.hit(1, 1))
        Assert.assertEquals(Pair(true, CellState.KILLED), model.hit(0, 9))
        // prev hot kill ship that's why (1, 9) was marked as hooted
        Assert.assertEquals(Pair(true, CellState.MISS), model.hit(1, 9))
        Assert.assertEquals(Pair(false, CellState.MISS), model.hit(3, 9))
        Assert.assertEquals(Pair(true, CellState.MISS), model.hit(3, 9))
    }

    fun testLongShip() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(0, 0)
        model.hit(2, 0)
        model.hit(3, 0)

        Assert.assertEquals(
            listOf(
                listOf(2, 1, 2, 2, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }

    fun testLongShip2() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(0, 0)
        model.hit(2, 0)
        model.hit(3, 0)
        model.hit(1, 0)

        Assert.assertEquals(
            listOf(
                listOf(3, 3, 3, 3, 4, 0, 0, 0, 0, 0),
                listOf(4, 4, 4, 4, 4, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }

    fun testMissShot() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(1, 1)
        model.hit(9, 9)
        model.hit(8, 8)
        Assert.assertEquals(
            listOf(
                listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(0, 4, 0, 0, 0, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 0, 0, 4, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 4)
            ),
            model.getMatrix()
        )
    }

    fun testHitInside() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(0, 7)
        model.hit(1, 7)
        Assert.assertEquals(
            listOf(
                listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 4, 1, 0, 0, 1, 1, 0, 0),
                listOf(4, 3, 4, 0, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 4, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }

    fun testHitOnBorders() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(0, 9)
        Assert.assertEquals(
            listOf(
                listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(0, 0, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 0, 1, 0, 0, 0, 0, 0, 0),
                listOf(3, 4, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }

    fun testHitOnBorders2() {
        val model = GameModel(emptyList())
        model.setMatrix(initField)
        model.hit(0, 5)
        Assert.assertEquals(
            listOf(
                listOf(1, 1, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 1, 0, 1),
                listOf(0, 1, 1, 1, 0, 1, 0, 1, 0, 0),
                listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
                listOf(4, 4, 1, 1, 0, 0, 0, 0, 0, 0),
                listOf(3, 4, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(4, 4, 0, 1, 0, 0, 1, 1, 0, 0),
                listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0),
                listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            ),
            model.getMatrix()
        )
    }
}

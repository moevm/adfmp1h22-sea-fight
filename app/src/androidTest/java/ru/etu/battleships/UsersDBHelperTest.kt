package ru.etu.battleships

import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase
import ru.etu.battleships.db.UsersDBHelper
import ru.etu.battleships.model.UserScore

class UsersDBHelperTest : TestCase() {
    lateinit var dbHelper: UsersDBHelper

    fun testDbInsertion() {
        dbHelper = UsersDBHelper(ApplicationProvider.getApplicationContext())
        dbHelper.recreateDb()
        dbHelper.addScoreForPair("qwe", "asd")
        dbHelper.addScoreForPair("qwe", "zxc")
        dbHelper.addScoreForPair("qwe", "zxc")
        dbHelper.addScoreForPair("qwe", "aaa")
        dbHelper.addScoreForPair("qwe", "aaa")
        dbHelper.addScoreForPair("qwe", "aaa")
        assertEquals(listOf(UserScore("qwe", 6)), dbHelper.getAllUserScores())
    }

    fun testWinnerLoserScore() {
        dbHelper = UsersDBHelper(ApplicationProvider.getApplicationContext())
        dbHelper.recreateDb()
        assertEquals(0, dbHelper.getWinnerLoserScore("qwe", "asd"))
        dbHelper.addScoreForPair("qwe", "asd")
        dbHelper.addScoreForPair("qwe", "zxc")
        dbHelper.addScoreForPair("qwe", "rty")
        assertEquals(1, dbHelper.getWinnerLoserScore("qwe", "asd"))
    }

    fun testLeaderBoard() {
        dbHelper = UsersDBHelper(ApplicationProvider.getApplicationContext())
        dbHelper.recreateDb()
        dbHelper.addScoreForPair("asd", "asd")
        dbHelper.addScoreForPair("asd", "zxc")
        dbHelper.addScoreForPair("qwe", "zxc")
        dbHelper.addScoreForPair("qwe", "aaa")
        dbHelper.addScoreForPair("qwe", "aaa")
        dbHelper.addScoreForPair("qwe", "aaa")
        dbHelper.addScoreForPair("zxc", "aaa")
        dbHelper.addScoreForPair("fgh", "aaa")
        assertEquals(
            listOf(
                UserScore("qwe", 4),
                UserScore("asd", 2),
                UserScore("fgh", 1),
                UserScore("zxc", 1),
            ).sortedWith(compareBy<UserScore> { it.score }.thenByDescending { it.username }),
            dbHelper.getAllUserScores()
        )
    }
}

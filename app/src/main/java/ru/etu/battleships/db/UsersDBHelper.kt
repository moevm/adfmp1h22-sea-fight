package ru.etu.battleships.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import ru.etu.battleships.model.GameScore
import ru.etu.battleships.model.UserScore

class UsersDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun recreateDb() {
        val db = writableDatabase
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun getAllUserScores(): List<UserScore> {
        val gameScores = ArrayList<GameScore>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var winner: String
        var loser: String
        var victories: Int
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                winner =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_WINNER))
                loser =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_LOSER))
                victories =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_SCORE))

                gameScores.add(GameScore(winner, loser, victories))
                cursor.moveToNext()
            }
        }
        return gameScores.groupBy { it.winner }
            .map { UserScore(it.key, it.value.sumOf { it.score }) }
            .sortedWith(compareBy<UserScore> { it.score }.thenByDescending { it.username })
    }

    fun addScoreForPair(winner: String, loser: String) {
        val db = this.writableDatabase
        val selectQuery =
            "SELECT  * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_WINNER} = ? AND ${DBContract.UserEntry.COLUMN_LOSER} = ?"

        db.rawQuery(selectQuery, arrayOf(winner, loser)).use {
            if (it.moveToFirst()) {
                val scores =
                    it.getInt(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_SCORE))
                updatePair(winner, loser, scores + 1)
                return
            }
        }
        insertPair(winner, loser)
    }

    fun getWinnerLoserScore(winner: String, loser: String): Int {
        val db = this.writableDatabase
        val selectQuery =
            "SELECT  * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_WINNER} = ? AND ${DBContract.UserEntry.COLUMN_LOSER} = ?"

        db.rawQuery(selectQuery, arrayOf(winner, loser)).use {
            if (it.moveToFirst()) {
                return it.getInt(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_SCORE))
            }
        }
        return 0
    }

    @Throws(SQLiteConstraintException::class)
    private fun insertPair(winner: String, loser: String): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_WINNER, winner)
        values.put(DBContract.UserEntry.COLUMN_LOSER, loser)
        values.put(DBContract.UserEntry.COLUMN_SCORE, 1)

        db.insert(DBContract.UserEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    private fun updatePair(winner: String, loser: String, score: Int): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_SCORE, score)

        val whereClause =
            "${DBContract.UserEntry.COLUMN_WINNER}=? AND ${DBContract.UserEntry.COLUMN_LOSER}=?"
        val whereArgs = arrayOf(winner, loser)
        db.update(DBContract.UserEntry.TABLE_NAME, values, whereClause, whereArgs)

        return true
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 2
        val DATABASE_NAME = "seafight.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" +
                DBContract.UserEntry.ID + " INT PRIMARY KEY," +
                DBContract.UserEntry.COLUMN_WINNER + " TEXT," +
                DBContract.UserEntry.COLUMN_LOSER + " TEXT," +
                DBContract.UserEntry.COLUMN_SCORE + " INT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }
}

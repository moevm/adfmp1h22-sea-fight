package ru.etu.battleships.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
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

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun readAllUsers(): ArrayList<UserScore> {
        val users = ArrayList<UserScore>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var username: String
        var victories: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                username =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_USERNAME))
                victories =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_VIRTORIES))

                users.add(UserScore(username, victories.toInt()))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun addVictoriesToPlayer(username: String) {
        val db = this.writableDatabase
        val selectQuery =
            "SELECT  * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_USERNAME} = ?"

        db.rawQuery(selectQuery, arrayOf(username)).use { // .use requires API 16
            if (it.moveToFirst()) {
                val victories = it.getInt(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_VIRTORIES))
                updateUser(username, victories + 1)
                return
            }
        }
        insertUser(username, 1)
    }

    @Throws(SQLiteConstraintException::class)
    private fun insertUser(username: String, victories: Int): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_USERNAME, username)
        values.put(DBContract.UserEntry.COLUMN_VIRTORIES, victories)

        db.insert(DBContract.UserEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    private fun updateUser(username: String, victories: Int): Boolean {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_VIRTORIES, victories)

        val whereClause = "${DBContract.UserEntry.COLUMN_USERNAME}=?"
        val whereArgs = arrayOf(username)
        db.update(DBContract.UserEntry.TABLE_NAME, values, whereClause, whereArgs)

        return true
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "seafight.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" +
                DBContract.UserEntry.COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                DBContract.UserEntry.COLUMN_VIRTORIES + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }
}

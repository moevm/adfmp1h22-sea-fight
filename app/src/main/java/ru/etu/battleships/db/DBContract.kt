package ru.etu.battleships.db

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_WINNER = "winner"
            val COLUMN_LOSER = "loser"
            val COLUMN_SCORE = "score"
        }
    }
}

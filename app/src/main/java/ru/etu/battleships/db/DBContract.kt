package ru.etu.battleships.db

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_USERNAME = "username"
            val COLUMN_VIRTORIES = "victories"
        }
    }
}

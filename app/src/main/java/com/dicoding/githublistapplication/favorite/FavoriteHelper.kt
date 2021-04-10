package com.dicoding.githublistapplication.favorite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.LOGIN
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.TABLE_NAME
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion._ID
import com.dicoding.githublistapplication.db.DatabaseHelper
import kotlin.jvm.Throws

class FavoriteHelper(context: Context) {
    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var dataBaseHelper: DatabaseHelper
        private var INSTANCE: FavoriteHelper? = null
        private lateinit var database: SQLiteDatabase
        fun getInstance(context: Context): FavoriteHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?:FavoriteHelper(context)
            }
    }
    init {
        dataBaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }
    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC")
    }
    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }
    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
    fun queryByName(name: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$LOGIN = ?",
            arrayOf(name),
            null,
            null,
            null,
            null)
    }
}
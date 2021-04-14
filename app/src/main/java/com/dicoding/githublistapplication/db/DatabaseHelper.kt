package com.dicoding.githublistapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "dbfavoriteapp"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.FavoriteColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.FavoriteColumns.NAME} TEXT NULL," +
                " ${DatabaseContract.FavoriteColumns.FOLLOWERS_URL} TEXT NULL," +
                " ${DatabaseContract.FavoriteColumns.FOLLOWING_URL} TEXT NULL,"+
                " ${DatabaseContract.FavoriteColumns.FOLLOWING} INTEGER NULL,"+
                " ${DatabaseContract.FavoriteColumns.FOLLOWERS} INTEGER NULL,"+
                " ${DatabaseContract.FavoriteColumns.AVATAR_URL} TEXT NULL,"+
                " ${DatabaseContract.FavoriteColumns.LOGIN} TEXT NULL,"+
                " ${DatabaseContract.FavoriteColumns.FAVORITE} TEXT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
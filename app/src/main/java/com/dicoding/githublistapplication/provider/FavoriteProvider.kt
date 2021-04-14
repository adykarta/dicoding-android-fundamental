package com.dicoding.githublistapplication.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.githublistapplication.dao.FavoriteHelper
import com.dicoding.githublistapplication.db.DatabaseContract.AUTHORITY
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.TABLE_NAME

class FavoriteProvider : ContentProvider() {
    companion object {
        private const val FAVORITE = 1
//        private const val FAVORITE_NAME = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favHelper: FavoriteHelper
        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE)
//            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAVORITE_NAME)
        }
    }

    override fun onCreate(): Boolean {
       favHelper =FavoriteHelper.getInstance(context as Context)
       favHelper.open()
        return true

    }
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when (sUriMatcher.match(uri)) {
            FAVORITE -> favHelper.queryAll()
//           FAVORITE_NAME -> favHelper.queryByName(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }
}

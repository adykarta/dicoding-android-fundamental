package com.dicoding.githublistapplication

import android.database.Cursor
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.AVATAR_URL
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.FAVORITE
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.FOLLOWERS
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.FOLLOWERS_URL
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.FOLLOWING
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.FOLLOWING_URL
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.LOGIN
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion.NAME
import com.dicoding.githublistapplication.db.DatabaseContract.FavoriteColumns.Companion._ID
import com.dicoding.githublistapplication.model.User

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<User> {
        val notesList = ArrayList<User>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val name = getString(getColumnIndexOrThrow(NAME))
                val followers_url = getString(getColumnIndexOrThrow(FOLLOWERS_URL))
                val following_url = getString(getColumnIndexOrThrow(FOLLOWING_URL))
                val followers = getInt(getColumnIndexOrThrow(FOLLOWERS))
                val following = getInt(getColumnIndexOrThrow(FOLLOWING))
                val login = getString(getColumnIndexOrThrow(LOGIN))
                val avatar_url = getString(getColumnIndexOrThrow(AVATAR_URL))
                val favorite = getString(getColumnIndexOrThrow(FAVORITE))
                notesList.add(
                    User(
                        id,
                        name,
                        followers_url,
                        following_url,
                        followers,
                        following,
                        avatar_url,
                        login,
                        favorite = favorite == "ya"
                    )
                )
            }
        }
        return notesList
    }
    fun mapCursorToUserObject(notesCursor: Cursor?): User {
        var user = User()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val name = getString(getColumnIndexOrThrow(NAME))
                val followers_url = getString(getColumnIndexOrThrow(FOLLOWERS_URL))
                val following_url = getString(getColumnIndexOrThrow(FOLLOWING_URL))
                val followers = getInt(getColumnIndexOrThrow(FOLLOWERS))
                val following = getInt(getColumnIndexOrThrow(FOLLOWING))
                val login = getString(getColumnIndexOrThrow(LOGIN))
                val avatar_url = getString(getColumnIndexOrThrow(AVATAR_URL))
                val favorite = getString(getColumnIndexOrThrow(FAVORITE))
                user = User(
                    id,
                    name,
                    followers_url,
                    following_url,
                    followers,
                    following,
                    avatar_url,
                    login,
                    favorite = favorite == "ya"
                )
            }
        }
        return user

    }
}
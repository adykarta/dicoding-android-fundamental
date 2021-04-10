package com.dicoding.githublistapplication.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class FavoriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val _ID = "_id"
            const val  NAME= "name"
            const val FOLLOWERS_URL = "followers_url"
            const val FOLLOWING_URL = "following_url"
            const val FOLLOWING = "following"
            const val FOLLOWERS = "followers"
            const val AVATAR_URL = "avatar_url"
            const val LOGIN = "login"
            const val FAVORITE = "favorite"

        }
    }
}

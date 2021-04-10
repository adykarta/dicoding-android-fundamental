package com.dicoding.githublistapplication
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class UserResponse {
    @SerializedName("items")
    var items = ArrayList<UserDetail>()
}

@Parcelize
class UserDetail(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("followers_url")
    var followersLink: String? = null,
    @SerializedName("following_url")
    var followingLink: String? = null,
    @SerializedName("followers")
    var followers: Int? = 0,
    @SerializedName("following")
    var following: Int? = 0,
    @SerializedName("avatar_url")
    var avatar_url: String? = null,
    @SerializedName("login")
    var login: String? = null,
    @SerializedName("favorite")
    var favorite: Boolean = false

): Parcelable

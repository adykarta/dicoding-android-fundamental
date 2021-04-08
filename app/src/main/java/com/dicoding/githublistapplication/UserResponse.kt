package com.dicoding.githublistapplication
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("items")
    var items = ArrayList<UserDetail>()
}


class UserDetail() : Parcelable {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("followers_url")
    var followersLink: String? = null
    @SerializedName("following_url")
    var followingLink: String? = null
    @SerializedName("followers")
    var followers: Int? = 0
    @SerializedName("following")
    var following: Int? = 0
    @SerializedName("avatar_url")
    var avatar_url: String? = null
    @SerializedName("login")
    var login: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        followers = parcel.readValue(Int::class.java.classLoader) as? Int
        following = parcel.readValue(Int::class.java.classLoader) as? Int
        avatar_url = parcel.readString()
        login = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeValue(followers)
        parcel.writeValue(following)
        parcel.writeString(avatar_url)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetail> {
        override fun createFromParcel(parcel: Parcel): UserDetail {
            return UserDetail(parcel)
        }

        override fun newArray(size: Int): Array<UserDetail?> {
            return arrayOfNulls(size)
        }
    }
}

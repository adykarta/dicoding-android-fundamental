package com.dicoding.githublistapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.githublistapplication.db.DatabaseContract
import com.dicoding.githublistapplication.favorite.FavoriteHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch



class DetailUserActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var favBtn: FloatingActionButton
    private var userDetailData: UserDetail? = null
    private var position: Int = 0
    private var isFavorite:Boolean = false
    private var userId:Int? = null
    companion object {
        const val EXTRA_USER = "extra_user"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
        const val EXTRA_POSITION = "extra_position"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)
        val tvNameReceived: TextView = findViewById(R.id.tv_name_received)
        val tvFollowingReceived: TextView = findViewById(R.id.tv_following_received)
        val tvFollowerReceived: TextView = findViewById(R.id.tv_follower_received)
        val tvAvatarReceived: ImageView = findViewById(R.id.img_photo_received)
        favBtn = findViewById(R.id.fab_add)
        favBtn.setOnClickListener(this)
        val user = intent?.getParcelableExtra<UserDetail>(EXTRA_USER) as UserDetail
        position = intent.getIntExtra(EXTRA_POSITION, 0)
        userDetailData = user
        Glide.with(this).load(user.avatar_url)
            .apply(RequestOptions().override(300, 300))
            .into(tvAvatarReceived)
        tvNameReceived.text = user.name
        tvFollowerReceived.text = "Followers: ${user.followers}"
        tvFollowingReceived.text = "Following: ${user.following}"
        supportActionBar?.title = user.login
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkUserInDatabase(user.login?:"")
        val sectionsPagerAdapter = SectionPagerAdapter(this, user)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f


    }

    private fun checkUserInDatabase(user:String){
        GlobalScope.launch(Dispatchers.Main) {
            val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = favoriteHelper.queryByName(user)
                MappingHelper.mapCursorToUserObject(cursor)
            }
            val userData = deferredNotes.await()
            if(userData.favorite){
                isFavorite = true
                userId = userData.id
                favBtn = findViewById(R.id.fab_add)
                val unwrappedDrawable: Drawable =
                    favBtn.getBackground()
                val wrappedDrawable =
                    DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    Color.RED
                )

            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    override fun onClick(v: View?) {
        if(v!==null){
            if(v.id==R.id.fab_add){
                val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
                favoriteHelper.open()
                val unwrappedDrawable: Drawable =
                    favBtn.getBackground()
                val wrappedDrawable =
                    DrawableCompat.wrap(unwrappedDrawable)
                if(userId ==null){
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        Color.RED
                    )
                    val values = ContentValues()
                    values.put(DatabaseContract.FavoriteColumns.NAME,userDetailData?.name )
                    values.put(DatabaseContract.FavoriteColumns.LOGIN, userDetailData?.login)
                    values.put(DatabaseContract.FavoriteColumns.AVATAR_URL, userDetailData?.avatar_url)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWERS_URL, userDetailData?.followersLink)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWING_URL, userDetailData?.followersLink)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWING, userDetailData?.following)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWERS, userDetailData?.followers)
                    values.put(DatabaseContract.FavoriteColumns.FAVORITE, "ya")
                    favoriteHelper.insert(values)
                }
                else{
                    favoriteHelper.deleteById(userId.toString())
                    DrawableCompat.setTint(
                        wrappedDrawable,
                        Color.WHITE
                    )
                }
                favoriteHelper.close()
            }
        }


    }



}
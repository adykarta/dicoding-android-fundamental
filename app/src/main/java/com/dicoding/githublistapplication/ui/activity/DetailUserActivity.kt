package com.dicoding.githublistapplication.ui.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.githublistapplication.MappingHelper
import com.dicoding.githublistapplication.R
import com.dicoding.githublistapplication.model.User
import com.dicoding.githublistapplication.db.DatabaseContract
import com.dicoding.githublistapplication.dao.FavoriteHelper
import com.dicoding.githublistapplication.databinding.ActivityDetailUserBinding
import com.dicoding.githublistapplication.ui.adapter.SectionPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailUserActivity : AppCompatActivity(), View.OnClickListener{
    private var binding: ActivityDetailUserBinding? = null
    private var UserData: User? = null
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
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val tvNameReceived=  binding?.tvNameReceived
        val tvFollowingReceived = binding?.tvFollowingReceived
        val tvFollowerReceived =binding?.tvFollowerReceived
        val tvAvatarReceived = binding?.imgPhotoReceived

        binding?.fabAdd?.setOnClickListener(this)
        val user = intent?.getParcelableExtra<User>(
            EXTRA_USER
        ) as User
        position = intent.getIntExtra(EXTRA_POSITION, 0)
        UserData = user
        if (tvAvatarReceived != null) {
            Glide.with(this).load(user.avatar_url)
                .apply(RequestOptions().override(300, 300))
                .into(tvAvatarReceived)
        }
        tvNameReceived?.text = user.name
        tvFollowerReceived?.text = "Followers: ${user.followers}"
        tvFollowingReceived?.text = "Following: ${user.following}"
        supportActionBar?.title = user.login
        checkUserInDatabase(user.login?:"")
        val sectionsPagerAdapter =
            SectionPagerAdapter(
                this,
                user
            )
        val viewPager= binding?.viewPager
        viewPager?.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        if (viewPager != null) {
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
        }
        supportActionBar?.elevation = 0f


    }


    private fun checkUserInDatabase(user:String){
        GlobalScope.launch(Dispatchers.Main) {
            val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = favoriteHelper.queryByName(user)
                MappingHelper.mapCursorToUserObject(
                    cursor
                )
            }
            val userData = deferredNotes.await()
            if(userData.favorite){
                isFavorite = true
                userId = userData.id
                binding?.fabAdd?.backgroundTintList = AppCompatResources.getColorStateList(this@DetailUserActivity, android.R.color.holo_red_dark)

            }
            else{
                isFavorite = false
                userId = null
                binding?.fabAdd?.backgroundTintList = AppCompatResources.getColorStateList(this@DetailUserActivity, android.R.color.white)
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.secondary_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home_menu) {
            val moveIntent = Intent(this@DetailUserActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }
        else if(item.itemId == R.id.favorite_menu){
            val moveIntent = Intent(this@DetailUserActivity, FavoriteActivity::class.java)
            startActivity(moveIntent)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if(v!==null){
            if(v.id== R.id.fab_add){
                val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
                favoriteHelper.open()
                checkUserInDatabase(UserData?.login?:"")
                if(userId ==null && isFavorite==false){
                    userId = UserData?.id
                    isFavorite = true
                    binding?.fabAdd?.backgroundTintList = AppCompatResources.getColorStateList(this@DetailUserActivity, android.R.color.holo_red_dark)
                    val values = ContentValues()
                    values.put(DatabaseContract.FavoriteColumns.NAME,UserData?.name )
                    values.put(DatabaseContract.FavoriteColumns.LOGIN, UserData?.login)
                    values.put(DatabaseContract.FavoriteColumns.AVATAR_URL, UserData?.avatar_url)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWERS_URL, UserData?.followersLink)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWING_URL, UserData?.followersLink)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWING, UserData?.following)
                    values.put(DatabaseContract.FavoriteColumns.FOLLOWERS, UserData?.followers)
                    values.put(DatabaseContract.FavoriteColumns.FAVORITE, "ya")
                    favoriteHelper.insert(values)
                }
                else{
                    favoriteHelper.deleteById(userId.toString())
                    binding?.fabAdd?.backgroundTintList = AppCompatResources.getColorStateList(this@DetailUserActivity, android.R.color.white)
                    userId = null
                    isFavorite = false
                }
                favoriteHelper.close()
            }
        }


    }



}
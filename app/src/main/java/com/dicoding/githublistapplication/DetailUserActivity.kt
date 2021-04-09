package com.dicoding.githublistapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DetailUserActivity : AppCompatActivity(){
    companion object {
        const val EXTRA_USER = "extra_user"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)
        val tvNameReceived: TextView = findViewById(R.id.tv_name_received)
        val tvFollowingReceived: TextView = findViewById(R.id.tv_following_received)
        val tvFollowerReceived: TextView = findViewById(R.id.tv_follower_received)
        val tvAvatarReceived: ImageView = findViewById(R.id.img_photo_received)
        val user = intent?.getParcelableExtra<UserDetail>(EXTRA_USER) as UserDetail
        Glide.with(this).load(user.avatar_url)
            .apply(RequestOptions().override(300, 300))
            .into(tvAvatarReceived)
        tvNameReceived.text = user.name
        tvFollowerReceived.text = "Followers: ${user.followers}"
        tvFollowingReceived.text = "Following: ${user.following}"
        supportActionBar?.title = user.login
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val sectionsPagerAdapter = SectionPagerAdapter(this, user)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
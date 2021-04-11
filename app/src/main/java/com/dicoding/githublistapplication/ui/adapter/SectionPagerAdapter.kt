package com.dicoding.githublistapplication.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.githublistapplication.model.User
import com.dicoding.githublistapplication.ui.fragment.FollowersFragment
import com.dicoding.githublistapplication.ui.fragment.FollowingFragment

class SectionPagerAdapter(activity: AppCompatActivity, user: User) : FragmentStateAdapter(activity) {
    var username: String = user.login!!

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment =
                FollowingFragment(
                    username
                )
            1 -> fragment =
                FollowersFragment(
                    username
                )
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }

}
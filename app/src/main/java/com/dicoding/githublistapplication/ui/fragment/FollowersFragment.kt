package com.dicoding.githublistapplication.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githublistapplication.*
import com.dicoding.githublistapplication.api.UserService
import com.dicoding.githublistapplication.model.User
import com.dicoding.githublistapplication.ui.activity.DetailUserActivity
import com.dicoding.githublistapplication.ui.activity.MainActivity
import com.dicoding.githublistapplication.ui.adapter.UserListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FollowersFragment(username: String) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var username: String = username
    private var rvUsers: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private lateinit var ctx: Context
    private var list: ArrayList<User> = arrayListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun getDetail(user: User){
        progressBar?.visibility = View.VISIBLE
        rvUsers?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getDetailUserData(user.login ?: "")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    val UserList = response.body()!!
                    user.followers= UserList.followers
                    user.following = UserList.following
                    user.name = UserList.name
                    progressBar?.visibility = View.GONE
                    rvUsers?.visibility = View.VISIBLE
                    showSelectedProject(user)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable?) {
                progressBar?.visibility = View.GONE
                rvUsers?.visibility = View.VISIBLE
                Toast.makeText(ctx
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun getListFollowersUser(username: String){
        rvUsers?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getFollowers(username)


        call.enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
                if (response.code() == 200) {
                    val UserList = response.body()
                    val listOfUser = arrayListOf<User>()
                    for (i in UserList) {
                        val user =
                            User()
                        user.name = i.name
                        user.login =i.login
                        user.avatar_url=i.avatar_url
                        user.followers = i.followers
                        user.following =i.following
                        user.followersLink = i.followersLink
                        user.followingLink = i.followingLink
                        listOfUser.add(user)
                    }
                    list = listOfUser
                    progressBar?.visibility = View.GONE
                    rvUsers?.visibility = View.VISIBLE
                    showRecyclerList(list)

                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable?) {
                progressBar?.visibility = View.GONE
                rvUsers?.visibility = View.VISIBLE
                Toast.makeText(ctx
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun showSelectedProject(user: User) {
        val moveWithDataIntent = Intent(ctx, DetailUserActivity::class.java)
        moveWithDataIntent.putExtra(DetailUserActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_followers, container, false)
        ctx = this.requireContext()
        getListFollowersUser(username = username)
        // Inflate the layout for this fragment
        return view

    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        rvUsers = itemView.findViewById(R.id.rv_users_followers)
        rvUsers?.setHasFixedSize(true)
        progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar_followers)
        progressBar?.visibility = View.VISIBLE
        rvUsers?.visibility = View.GONE
        super.onViewCreated(itemView, savedInstanceState)
        showRecyclerList(list)

    }
    private fun showRecyclerList(list: ArrayList<User>) {
        rvUsers?.layoutManager = LinearLayoutManager(ctx)
        val listUserAdapter =
            UserListAdapter(list)
        rvUsers?.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object :
            UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                getDetail(data)
            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowersFragment("").apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
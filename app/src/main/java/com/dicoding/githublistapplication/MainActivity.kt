package com.dicoding.githublistapplication

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var rvUsers: RecyclerView
    private lateinit var edtsearch: EditText

    private var list: ArrayList<UserDetail> = arrayListOf()
    companion object {
        val BASE_URL = "https://api.github.com/"

    }
    fun getDetail(user:UserDetail){
        var progressBar: ProgressBar = findViewById(R.id.progress_bar)

        progressBar.setVisibility(View.VISIBLE)
        rvUsers.setVisibility(View.GONE)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UserService::class.java)
        val call = service.getDetailUserData(user.login!!)


        call.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.code() == 200) {
                    val userResponse = response.body()!!
                    user.followers= userResponse.followers
                    user.following = userResponse.following
                    user.name = userResponse.name
                    progressBar.setVisibility(View.GONE)
                    rvUsers.setVisibility(View.VISIBLE)
                    showSelectedProject(user)
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable?) {
                progressBar.setVisibility(View.GONE)
                rvUsers.setVisibility(View.VISIBLE)
                Toast.makeText(this@MainActivity
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun searchUser(username:String){
       var progressBar: ProgressBar = findViewById(R.id.progress_bar)
        progressBar.setVisibility(View.VISIBLE)
        rvUsers.setVisibility(View.GONE)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(UserService::class.java)
        val call = service.getSearchUserData(username)


        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.code() == 200) {
                   val userResponse = response.body()!!
                    val listOfUser = arrayListOf<UserDetail>()
                    for (i in userResponse.items.indices) {
                        val user = UserDetail()
                        user.name = userResponse.items[i].name
                        user.login = userResponse.items[i].login
                        user.avatar_url=userResponse.items[i].avatar_url
                        user.followers = userResponse.items[i].followers
                        user.following = userResponse.items[i].following
                        user.followersLink = userResponse.items[i].followersLink
                        user.followingLink = userResponse.items[i].followingLink
                        listOfUser.add(user)
                    }
                    list = listOfUser
                    progressBar.setVisibility(View.GONE)
                    showRecyclerList(list)

                    rvUsers.setVisibility(View.VISIBLE)

                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable?) {
                progressBar.setVisibility(View.GONE)
                rvUsers.setVisibility(View.VISIBLE)
                Toast.makeText(this@MainActivity
                   , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            rvUsers = findViewById(R.id.rv_users)
            rvUsers.setHasFixedSize(true)
            supportActionBar?.title = "Github"
            var progressBar: ProgressBar = findViewById(R.id.progress_bar)
            progressBar.setVisibility(View.GONE)
            showRecyclerList(list)


    }
    private fun showSelectedProject(user: UserDetail) {
        val moveWithDataIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
        moveWithDataIntent.putExtra(DetailUserActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)

    }
    private fun showRecyclerList(list: ArrayList<UserDetail>) {
        rvUsers.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = UserListAdapter(list)
        rvUsers.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserDetail) {
                getDetail(data)
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search_menu).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchUser(query)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {

    }


}
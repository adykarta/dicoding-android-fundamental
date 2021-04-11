package com.dicoding.githublistapplication.ui.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githublistapplication.R
import com.dicoding.githublistapplication.api.UserService
import com.dicoding.githublistapplication.databinding.ActivityMainBinding
import com.dicoding.githublistapplication.model.User
import com.dicoding.githublistapplication.model.UserList
import com.dicoding.githublistapplication.ui.adapter.UserListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var list: ArrayList<User> = arrayListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    companion object {
        val BASE_URL = "https://api.github.com/"

    }
    private fun getDetail(user: User){
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.rvUsers?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getDetailUserData(user.login ?: "")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    val UserList = response.body()
                    user.followers= UserList.followers
                    user.following = UserList.following
                    user.name = UserList.name
                    binding?.progressBar?.visibility = View.GONE
                    binding?.rvUsers?.visibility = View.VISIBLE
                    showSelectedProject(user)
                }
                else{
                    Toast.makeText(this@MainActivity
                        , "Error with status code" + response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable?) {
                binding?.progressBar?.visibility = View.GONE
                binding?.rvUsers?.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun searchUser(username:String){

        binding?.progressBar?.visibility = View.VISIBLE
        binding?.rvUsers?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getSearchUserData(username)


        call.enqueue(object : Callback<UserList> {
            override fun onResponse(call: Call<UserList>, response: Response<UserList>) {

                if (response.code() == 200) {
                   val UserList = response.body()
                    val listOfUser = arrayListOf<User>()
                    for (i in UserList.items.indices) {
                        val user =
                            User()
                        user.name = UserList.items[i].name
                        user.login = UserList.items[i].login
                        user.avatar_url=UserList.items[i].avatar_url
                        user.followers = UserList.items[i].followers
                        user.following = UserList.items[i].following
                        user.followersLink = UserList.items[i].followersLink
                        user.followingLink = UserList.items[i].followingLink
                        listOfUser.add(user)
                    }
                    list = listOfUser
                    binding?.progressBar?.visibility = View.GONE
                    binding?.rvUsers?.visibility = View.VISIBLE
                    showRecyclerList(list)
                }
            }

            override fun onFailure(call: Call<UserList>, t: Throwable?) {
                binding?.progressBar?.visibility = View.GONE
                binding?.rvUsers?.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity
                   , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding?.root)
            binding?.rvUsers?.setHasFixedSize(true)
            supportActionBar?.title = "Github"
            binding?.progressBar?.visibility = View.GONE
            showRecyclerList(list)


    }
    private fun showSelectedProject(user: User) {
        val moveWithDataIntent = Intent(this@MainActivity, DetailUserActivity::class.java)
        moveWithDataIntent.putExtra(DetailUserActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)
    }

    private fun showRecyclerList(list: ArrayList<User>) {
        binding?.rvUsers?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter =
            UserListAdapter(list)
       binding?. rvUsers?.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
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
        else if(item.itemId == R.id.favorite_menu){
            val moveIntent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(moveIntent)

        }
        return super.onOptionsItemSelected(item)
    }



}
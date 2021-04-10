package com.dicoding.githublistapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githublistapplication.favorite.FavoriteHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoriteActivity : AppCompatActivity() {
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var list: ArrayList<UserDetail> = arrayListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        rvUsers = findViewById(R.id.rv_users_favorite)
        rvUsers.setHasFixedSize(true)
        supportActionBar?.title = "Favorite User"
        progressBar = findViewById(R.id.progress_bar_favorite)
        progressBar?.visibility = View.GONE
        //set back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            // proses ambil data
            loadFavoriteAsync()
        } else {
            val listData = savedInstanceState.getParcelableArrayList<UserDetail>(EXTRA_STATE)
            if (listData != null) {
                showRecyclerList(listData)
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, list)
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = favoriteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
//            favoriteHelper.close()
            progressBar.visibility = View.GONE
            val favorites = deferredNotes.await()
            if (favorites.size > 0) {
                showRecyclerList(favorites)
            } else {
                showRecyclerList(list)
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }


    private fun getDetail(user:UserDetail){

        progressBar?.visibility = View.VISIBLE
        rvUsers?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getDetailUserData(user.login ?: "")
        call.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.code() == 200) {
                    val userResponse = response.body()
                    user.followers= userResponse.followers
                    user.following = userResponse.following
                    user.name = userResponse.name
                    progressBar?.visibility = View.GONE
                    rvUsers?.visibility = View.VISIBLE
                    showSelectedProject(user)
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable?) {
                progressBar?.visibility = View.GONE
                rvUsers?.visibility = View.VISIBLE
                Toast.makeText(this@FavoriteActivity
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showSelectedProject(user: UserDetail) {
        val moveWithDataIntent = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
        moveWithDataIntent.putExtra(DetailUserActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)
    }

    private fun showRecyclerList(listUser: ArrayList<UserDetail>) {
        list = listUser
        rvUsers.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = UserListAdapter(listUser)
        rvUsers.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserDetail) {
               getDetail(data)
            }
        })

    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rvUsers, message, Snackbar.LENGTH_SHORT).show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
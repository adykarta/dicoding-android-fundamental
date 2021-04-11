package com.dicoding.githublistapplication.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githublistapplication.*
import com.dicoding.githublistapplication.api.UserService
import com.dicoding.githublistapplication.dao.FavoriteHelper
import com.dicoding.githublistapplication.databinding.ActivityFavoriteBinding
import com.dicoding.githublistapplication.db.DatabaseContract
import com.dicoding.githublistapplication.model.User
import com.dicoding.githublistapplication.ui.adapter.UserListAdapter
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
    private var binding: ActivityFavoriteBinding? = null
    private var list: ArrayList<User> = arrayListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.rvUsersFavorite?.setHasFixedSize(true)
        supportActionBar?.title = "Favorite User"
        binding?.progressBarFavorite?.visibility = View.GONE
        //set back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            // proses ambil data
            loadFavoriteAsync()
        } else {
            val listData = savedInstanceState.getParcelableArrayList<User>(
                EXTRA_STATE
            )
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
            binding?.progressBarFavorite?.visibility = View.VISIBLE
            val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
            favoriteHelper.open()
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(DatabaseContract.FavoriteColumns.CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(
                    cursor
                )
            }
//            favoriteHelper.close()
            binding?.progressBarFavorite?.visibility = View.GONE
            val favorites = deferredNotes.await()
            if (favorites.size > 0) {
                showRecyclerList(favorites)
            } else {
                showRecyclerList(list)
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }
    }


    private fun getDetail(user: User){

        binding?.progressBarFavorite?.visibility = View.VISIBLE
        binding?.rvUsersFavorite?.visibility = View.GONE
        val service = retrofit.create(UserService::class.java)
        val call = service.getDetailUserData(user.login ?: "")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    val UserList = response.body()
                    user.followers= UserList.followers
                    user.following = UserList.following
                    user.name = UserList.name
                    binding?.progressBarFavorite?.visibility = View.GONE
                    binding?.rvUsersFavorite?.visibility = View.VISIBLE
                    showSelectedProject(user)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable?) {
                binding?.progressBarFavorite?.visibility = View.GONE
                binding?.rvUsersFavorite?.visibility = View.VISIBLE
                Toast.makeText(this@FavoriteActivity
                    , "Failed to fetch data",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showSelectedProject(user: User) {
        val moveWithDataIntent = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
        moveWithDataIntent.putExtra(DetailUserActivity.EXTRA_USER,user)
        startActivity(moveWithDataIntent)
    }

    private fun showRecyclerList(listUser: ArrayList<User>) {
        list = listUser
        binding?.rvUsersFavorite?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter =
            UserListAdapter(
                listUser
            )
        binding?.rvUsersFavorite?.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : UserListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
               getDetail(data)
            }
        })

    }

    private fun showSnackbarMessage(message: String) {
        binding?.rvUsersFavorite?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
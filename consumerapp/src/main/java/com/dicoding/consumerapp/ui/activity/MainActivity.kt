package com.dicoding.consumerapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.consumerapp.MappingHelper
import com.dicoding.consumerapp.databinding.ActivityMainBinding
import com.dicoding.consumerapp.db.DatabaseContract
import com.dicoding.consumerapp.model.User
import com.dicoding.consumerapp.ui.adapter.UserListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    private var list: ArrayList<User> = arrayListOf()

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.rvUsersFavorite?.setHasFixedSize(true)
        supportActionBar?.title = "Favorite User"
        binding?.progressBarFavorite?.visibility = View.GONE
//        //set back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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



    private fun showRecyclerList(listUser: ArrayList<User>) {
        list = listUser
        binding?.rvUsersFavorite?.layoutManager = LinearLayoutManager(this)
        val listUserAdapter =
            UserListAdapter(
                listUser
            )
        binding?.rvUsersFavorite?.adapter = listUserAdapter



    }

    private fun showSnackbarMessage(message: String) {
        binding?.rvUsersFavorite?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
}
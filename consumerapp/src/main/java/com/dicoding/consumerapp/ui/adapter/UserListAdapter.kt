package com.dicoding.consumerapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.consumerapp.R
import com.dicoding.consumerapp.model.User

class UserListAdapter(private val listUser: ArrayList<User>) : RecyclerView.Adapter<UserListAdapter.ListViewHolder>() {


    var listFav = ArrayList<User>()
        set(listNotes) {
            if (listNotes.size > 0) {
                this.listUser.clear()
            }
            this.listUser.addAll(listFav)

            notifyDataSetChanged()
        }

    fun addItem(user: User) {
        this.listFav.add(user)
        notifyItemInserted(this.listFav.size - 1)
    }

    fun updateItem(position: Int, user: User) {
        this.listFav[position] = user
        notifyItemChanged(position, user)
    }

    fun removeItem(position: Int) {
        this.listFav.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listFav.size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row_user, viewGroup, false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        Glide.with(holder.itemView.context).load(user.avatar_url)
            .apply(RequestOptions().override(200, 200))
            .into(holder.imgPhoto)
        holder.tvUsername.text = user.login
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvUsername: TextView = itemView.findViewById(R.id.tv_item_username)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

}
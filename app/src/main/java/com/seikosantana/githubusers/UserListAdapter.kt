package com.seikosantana.githubusers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide

class UserListAdapter(private val data: ArrayList<UserMain>): Adapter<UserListAdapter.UserListViewHolder>() {
    class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView = itemView.findViewById<ImageView>(R.id.imgAvatar)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvRepositories = itemView.findViewById<TextView>(R.id.tvRepositories)
        val tvFollower = itemView.findViewById<TextView>(R.id.tvFollower)
        val cardItem = itemView.findViewById<CardView>(R.id.cardItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserListViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        val user = data[position]
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(user.avatarUrl)
            .override(70, 70)
            .into(holder.avatarImageView)
        holder.tvUsername.text = user.username
        holder.tvRepositories.text = holder.itemView.context.getString(R.string.repositories_placeholder, user.repositoryCount)
        holder.tvFollower.text = holder.itemView.context.getString(R.string.followers_placeholder, user.followerCount)
        holder.cardItem.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserDetailsActivity::class.java)
            intent.putExtra("username", user.username)
            startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
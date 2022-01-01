package com.seikosantana.githubusers

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RepositoryListAdapter(private val repositories: ArrayList<Repository>): RecyclerView.Adapter<RepositoryListAdapter.RepositoryViewHolder>() {

    class RepositoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val lblRepository = itemView.findViewById<TextView>(R.id.lblRepositoryName)
        val card = itemView.findViewById<CardView>(R.id.cardRepository)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repo = repositories[position]
        holder.lblRepository.text = repo.name
        holder.card.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.url))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return repositories.size
    }
}
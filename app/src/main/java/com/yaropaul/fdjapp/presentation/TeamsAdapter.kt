package com.yaropaul.fdjapp.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.yaropaul.fdjapp.R
import com.yaropaul.fdjapp.data.model.Team
import com.yaropaul.fdjapp.databinding.ItemTeamBinding

class TeamsAdapter(data  : List<Team>, private  val context: Context) : RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {
    private var items: List<Team> ? = data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTeamBinding.inflate(inflater,parent,false)
        return TeamsViewHolder(binding, context)
    }

    override fun getItemCount() = items?.size?: 0

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        items?.get(position)?.let { holder.bind(it) }
    }

    inner class TeamsViewHolder(private val binding: ItemTeamBinding, private  val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.apply {
                Glide
                    .with(context)
                    .load(team.strTeamBadge)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.placeholder)
                    .into(cardViewImage);
            }
        }
    }
}
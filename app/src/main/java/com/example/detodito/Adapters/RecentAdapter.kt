package com.example.detodito.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.R
import com.example.detodito.ViewHolders.RecentViewHolder

class RecentAdapter(val data: MutableList<String>, val contexto: Context): RecyclerView.Adapter<RecentViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecentViewHolder(layoutInflater.inflate(R.layout.item_search_recent_categories, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val item: String = data[position];
        holder.bin(item, contexto)
    }

}
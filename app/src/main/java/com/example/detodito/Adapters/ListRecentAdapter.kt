package com.example.detodito.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.R
import com.example.detodito.ViewHolders.SRecentViewHolder

class ListRecentAdapter(
    val data: MutableList<String>,
    val applicationContext: Context
): RecyclerView.Adapter<SRecentViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SRecentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_search, parent, false)
        return SRecentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SRecentViewHolder, position: Int) {
        val item: String = data[position];
        holder.bin(item, applicationContext)
    }

}
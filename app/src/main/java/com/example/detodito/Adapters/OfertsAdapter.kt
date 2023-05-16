package com.example.detodito.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.R
import com.example.detodito.ViewHolders.OfertsViewHolder

class OfertsAdapter(val data: MutableList<String>, val contexto: FragmentActivity): RecyclerView.Adapter<OfertsViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return OfertsViewHolder(layoutInflater.inflate(R.layout.item_oferts, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: OfertsViewHolder, position: Int) {
        val item: String = data[position];
        holder.bin(contexto)
    }

}
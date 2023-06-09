package com.example.detodito.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.R
import com.example.detodito.ViewHolders.CategoriesViewHolder
import com.example.detodito.data.categoriesItem

class CategoriesAdapter(val data: MutableList<categoriesItem>, val contexto: FragmentActivity): RecyclerView.Adapter<CategoriesViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val item: categoriesItem = data[position];
        holder.bin(item, contexto)
    }

}
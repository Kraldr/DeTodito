package com.example.detodito.ViewHolders

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.detodito.data.categoriesItem
import com.example.detodito.databinding.ItemCategoriesBinding


class CategoriesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemCategoriesBinding.bind(view)

    fun bin (page: categoriesItem, contexto: FragmentActivity) {

        binding.btnPage.setOnClickListener {

        }
        binding.txtNameAni.text = page.name

        Glide.with(contexto)
            .load(page.url)
            .into(binding.imageCategories)
    }
}
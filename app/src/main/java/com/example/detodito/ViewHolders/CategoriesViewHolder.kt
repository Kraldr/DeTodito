package com.example.detodito.ViewHolders

import android.app.AlertDialog
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.databinding.ItemCategoriesBinding


class CategoriesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemCategoriesBinding.bind(view)

    fun bin (page: String, contexto: FragmentActivity) {

        binding.btnPage.setOnClickListener {

        }
        binding.txtNameAni.text = page
    }
}
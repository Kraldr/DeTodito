package com.example.detodito.ViewHolders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.databinding.ItemSearchBinding


class SRecentViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = ItemSearchBinding.bind(view)

    fun bin (
        itemIMG: String,
        applicationContext: Context,
    ) {

        binding.textViewServer.text = itemIMG

        binding.btnServer.setOnClickListener {

        }


    }
}
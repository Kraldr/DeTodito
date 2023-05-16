package com.example.detodito

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.detodito.Adapters.RecentAdapter
import com.example.detodito.databinding.ActivitySearchBinding

class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#FFFFFF") // Reemplaza #FF0000 con el color deseado
        }

        val dataAll = mutableListOf<String>()

        dataAll.add("Uno")
        dataAll.add("Dos")
        dataAll.add("Tres")
        dataAll.add("Cuatro")
        dataAll.add("Cinco")
        dataAll.add("Seis")
        dataAll.add("Siete")
        dataAll.add("Ocho")
        dataAll.add("Nueve")
        dataAll.add("Diez")

        initRecyclerViewPopular(dataAll)

    }

    private fun initRecyclerViewPopular(data: MutableList<String>) {
        val refresh = Handler(Looper.getMainLooper())
        refresh.post(kotlinx.coroutines.Runnable {
            binding.recyViewHorizontal.apply {
                layoutManager = LinearLayoutManager(
                    applicationContext,
                    LinearLayoutManager.HORIZONTAL,
                    true
                )
                scrollToPosition(8)
                adapter = RecentAdapter(data, applicationContext)
                //addItemDecoration(HomeFragment.SpacesItemDecoration(8))
            }
        })

    }

}
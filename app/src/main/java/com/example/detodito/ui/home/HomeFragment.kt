package com.example.detodito.ui.home

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.detodito.Adapters.CategoriesAdapter
import com.example.detodito.Adapters.OfertsAdapter
import com.example.detodito.Search
import com.example.detodito.data.categories
import com.example.detodito.data.categoriesItem
import com.example.detodito.databinding.FragmentHomeBinding
import com.google.gson.Gson
import okhttp3.*
import okio.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dataAll = mutableListOf<String>()

        dataAll.add("Uno")
        dataAll.add("Dos")
        dataAll.add("Tres")

        val dataCategories = mutableListOf<String>()

        dataCategories.add("Uno")
        dataCategories.add("Dos")
        dataCategories.add("Tres")
        dataCategories.add("Cuatro")
        dataCategories.add("Cinco")
        dataCategories.add("Seis")
        dataCategories.add("Siete")
        dataCategories.add("Ocho")
        dataCategories.add("Nueve")
        dataCategories.add("Diez")

        initRecyclerViewPopular(dataAll)

        val url = "http://10.0.2.2:3000/categories"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar errores
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val gson = Gson()
                val categories = gson.fromJson(responseData, categories::class.java)
                val categoriesArray = mutableListOf<categoriesItem>()
                for (element in categories) {
                    categoriesArray.add(element)
                }

                val refresh = Handler(Looper.getMainLooper())
                refresh.post(kotlinx.coroutines.Runnable {
                    initCategories (categoriesArray)
                })

                // Aquí puedes hacer lo que necesites con el array de categorías
            }
        })

        binding.btnSearch.setOnClickListener {
            val intent = Intent(requireActivity(), Search::class.java)
            startActivity(intent)
        }


        return root
    }

    private fun initRecyclerViewPopular(data: MutableList<String>) {
        val refresh = Handler(Looper.getMainLooper())
        refresh.post(kotlinx.coroutines.Runnable {
            binding.recyView.apply {
                layoutManager = LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    true
                )
                scrollToPosition(2)
                adapter = OfertsAdapter(data, requireActivity())
                addItemDecoration(SpacesItemDecoration(8))
            }
        })

    }

    private fun initCategories (data: MutableList<categoriesItem>) {
        val refresh = Handler(Looper.getMainLooper())
        refresh.post(kotlinx.coroutines.Runnable {
            binding.recyclerCategories.apply {
                layoutManager = GridLayoutManager(requireActivity(), 4)
                val adapter = CategoriesAdapter(data, requireActivity())
                this.adapter = adapter
                addItemDecoration(SpacesItemDecoration(8))
            }
        })
    }

    class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space

            if (parent.getChildLayoutPosition(view) < 1) {
                outRect.top = space
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
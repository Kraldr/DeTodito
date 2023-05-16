package com.example.detodito

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.detodito.data.categories
import com.example.detodito.data.categoriesItem
import com.example.detodito.data.dataRegis
import com.example.detodito.databinding.ActivityManagementBinding
import com.example.detodito.databinding.ActivitySignupBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class Management : AppCompatActivity() {

    private lateinit var binding: ActivityManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_management)

        binding = ActivityManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una opción:")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor =
                Color.parseColor("#FFFFFF")
        }

        var url = ""
        val jsonBody= JSONObject()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val types = mutableListOf<String>()
        types.add("Para tu hogar")
        types.add("Para ti y tu familia")
        types.add("Para tu mascota")
        types.add("Para tu negocio")

        binding.typeInputLayout.setEndIconOnClickListener{
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types)
            builder.setAdapter(adapter) { _, which ->
                val data = types[which]
                binding.typeCompleteTextView.setText(data)
            }

            builder.create().show()
        }

        binding.updateButton.setOnClickListener {
            url = "http://10.0.2.2:3000/category"
            dialog.show()
            jsonBody.put("name", binding.categoriesEditText.text.toString())
            jsonBody.put("url", binding.urlcateEditText.text.toString())
            jsonBody.put("type", binding.typeCompleteTextView.text.toString())
            val requestBody =
                jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    dialog.dismiss()
                    Log.d("onFailure", e.toString())
                    // Manejar errores
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val handler = Handler(Looper.getMainLooper())
                    if (response.isSuccessful && responseData != null) {
                        dialog.dismiss()
                        handler.post {
                            Toast.makeText(applicationContext, "Categoría creada correctamente", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        dialog.dismiss()
                        handler.post {
                            Toast.makeText(applicationContext, "Error al crear la categoría", Toast.LENGTH_LONG).show()
                        }
                        Log.d("NOonResponse", response.toString())
                    }
                }
            })
        }

        binding.typeSecondInputLayout.setEndIconOnClickListener {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types)
            builder.setAdapter(adapter) { _, which ->
                val data = types[which]
                binding.typeSecondCompleteTextView.setText(data)
                binding.typecategoInputLayout.isEnabled = true
                binding.typeCategoCompleteTextView.setText("")
            }

            builder.create().show()
        }

        binding.typecategoInputLayout.setEndIconOnClickListener {
            url = "http://10.0.2.2:3000/categories"
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
                        val nameCategories = mutableListOf<String>()
                        for (element in categoriesArray) {
                            if (binding.typeSecondCompleteTextView.text.toString() == element.type) {
                                nameCategories.add(element.name)
                            }
                        }
                        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, nameCategories)
                        builder.setAdapter(adapter) { _, which ->
                            val data = nameCategories[which]
                            binding.typeCategoCompleteTextView.setText(data)
                        }

                        builder.create().show()
                    })

                    // Aquí puedes hacer lo que necesites con el array de categorías
                }
            })
        }
    }

}
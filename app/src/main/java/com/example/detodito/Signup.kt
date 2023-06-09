package com.example.detodito

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.detodito.data.Departament
import com.example.detodito.data.dataRegis
import com.example.detodito.databinding.ActivitySignupBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import org.jsoup.Jsoup

class Signup : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    val job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    var department = ""
    private lateinit var dataP:Departament
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor =
                Color.parseColor("#FFFFFF")
        }

        val url = "http://10.0.2.2:3000/signup"
        val jsonBody = JSONObject()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        binding.ciudadInputLayout.setEndIconOnClickListener {
            val departamentosEncontrados = dataP.filter { it.departamento == department }

            val ciudades = mutableListOf<String>()
            for (department in departamentosEncontrados[0].ciudades) {
                ciudades.add(department)
            }
            mostrarListaDepartamentos(ciudades, 2)
        }

        binding.departamentoInputLayout.setEndIconOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result =
                        Jsoup.connect("https://raw.githubusercontent.com/marcovega/colombia-json/master/colombia.min.json")
                            .ignoreContentType(true).execute().body()
                    val gson = Gson()
                    val departments = gson.fromJson(result, Departament::class.java)

                    // hacer algo con los datos, como mostrarlos en una lista
                    val departamentos = mutableListOf<String>()
                    for (department in departments) {
                        departamentos.add(department.departamento)
                    }
                    val refresh = Handler(Looper.getMainLooper())
                    refresh.post(kotlinx.coroutines.Runnable {
                        dataP = departments
                        mostrarListaDepartamentos(departamentos, 1)
                    })
                } catch (e: Exception) {
                    Log.e("ERROR", e.message, e)
                }
            }
        }

        binding.registerButton.setOnClickListener {
            dialog.show()
            jsonBody.put("email", binding.emailEditText.text.toString())
            jsonBody.put("password", binding.passwordEditText.text.toString())
            jsonBody.put("phone", binding.celularEditText.text.toString())
            jsonBody.put("firstName", binding.firstNameEditText.text.toString())
            jsonBody.put("lastName", binding.lastNameEditText.text.toString())
            jsonBody.put("doc", binding.docEditText.text.toString())
            jsonBody.put("address", binding.addressEditText.text.toString())
            jsonBody.put("dept", binding.autoCompleteTextView.text.toString())
            jsonBody.put("ciudad", binding.ciudadautoCompleteTextView.text.toString())
            jsonBody.put("type", "Usuario")
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
                    if (response.isSuccessful && responseData != null) {
                        val gson = Gson()
                        val userData = gson.fromJson(responseData, dataRegis::class.java)
                        shared(userData.uid, true)
                        dialog.dismiss()
                        finish()
                        Toast.makeText(applicationContext, "Cuenta creada correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        Log.d("NOonResponse", response.toString())
                    }
                }
            })
        }

    }

    fun mostrarListaDepartamentos(departamentos: MutableList<String>, local:Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una opción:")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, departamentos)
        builder.setAdapter(adapter) { _, which ->
            val data = departamentos[which]
            if (local == 1) {
                binding.ciudadInputLayout.isEnabled = true
                binding.autoCompleteTextView.setText(data)
                binding.ciudadautoCompleteTextView.setText("")
                department = data
            }else if (local == 2) {
                binding.ciudadautoCompleteTextView.setText(data)
            }else {
            }
        }

        builder.create().show()
    }


    private fun shared(myUID: String, state: Boolean) {
        val sharedPref =
            applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString("myUID", myUID)
            putBoolean("state", state)
            apply()
        }
    }
}
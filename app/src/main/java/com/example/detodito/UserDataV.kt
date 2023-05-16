package com.example.detodito

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.detodito.data.UserData
import com.example.detodito.databinding.ActivityDatauservBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class UserDataV : AppCompatActivity() {

    private lateinit var binding: ActivityDatauservBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatauservBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (myUID, state) = loadData()

        // Enviar el token de identificación al servidor
        val url = "http://10.0.2.2:3000/login"
        val jsonBody = JSONObject()
        jsonBody.put("idToken", myUID)
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object :
            Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar errores
            }

            override fun onResponse(
                call: Call,
                response: Response
            ) {
                val responseData = response.body?.string()
                val gson = Gson()
                val userData = gson.fromJson(responseData, UserData::class.java)
                val refresh = Handler(Looper.getMainLooper())
                refresh.post(kotlinx.coroutines.Runnable {
                    binding.firstNameEditText.setText(userData.userData.firstName)
                    binding.lastNameEditText.setText(userData.userData.lastName)
                    binding.emailEditText.setText(userData.userData.email)
                    binding.docEditText.setText(userData.userData.doc)
                    binding.addressEditText.setText(userData.userData.address)
                })
            }
        })
    }

    private fun loadData(): Pair<String?, Boolean?> {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val myUID = sharedPref?.getString("myUID", null).toString()
        val state = sharedPref?.getBoolean("state", false)
        return Pair(myUID, state)
    }
}
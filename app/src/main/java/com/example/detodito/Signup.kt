package com.example.detodito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.detodito.data.UserData
import com.example.detodito.data.dataRegis
import com.example.detodito.databinding.ActivityDatauservBinding
import com.example.detodito.databinding.ActivitySignupBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = "http://10.0.2.2:3000/signup"
        val jsonBody = JSONObject()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        binding.registerButton.setOnClickListener {
            dialog.show()
            jsonBody.put("email", binding.emailEditText.text.toString())
            jsonBody.put("password", binding.passwordEditText.text.toString())
            jsonBody.put("phone", binding.celularEditText.text.toString())
            jsonBody.put("firstName", binding.firstNameEditText.text.toString())
            jsonBody.put("lastName", binding.lastNameEditText.text.toString())
            jsonBody.put("doc", binding.docEditText.text.toString())
            jsonBody.put("address", binding.addressEditText.text.toString())
            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    dialog.dismiss()
                    // Manejar errores
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    if (response.isSuccessful && responseData != null) {
                        val gson = Gson()
                        val userData = gson.fromJson(responseData, dataRegis::class.java)
                        shared(userData.uid, true)
                        dialog.dismiss()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Manejar respuesta fallida
                    }
                }
            })
        }

    }

    private fun shared(myUID: String, state: Boolean) {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("myUID", myUID)
            putBoolean("state", state)
            apply()
        }
    }
}
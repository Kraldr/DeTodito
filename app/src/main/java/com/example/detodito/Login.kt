package com.example.detodito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.detodito.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()


        val (myUID, state) = loadData()

        if (state == true) {
            dialog.show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("myUID", myUID)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                dialog.show()
                try {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                user?.getIdToken(true)?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val idToken = task.result?.token
                                        if (idToken != null) {
                                            shared(idToken, true)
                                        }
                                        val intent = Intent(applicationContext, MainActivity::class.java)
                                        intent.putExtra("token", idToken)
                                        dialog.dismiss()
                                        startActivity(intent)
                                        Toast.makeText(
                                            baseContext, "Authentication Successful.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(
                                            baseContext, "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } catch (e: Exception) {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    println(e.toString())
                }
            }

            if (password.isEmpty()) {
                // Set error text
                binding.passwordLayout.error = "Ingresa una contraseña"
            }

            if (email.isEmpty()) {
                binding.emailLayout.error = "Ingresa un correo valido"
            }
        }

        binding.register.setOnClickListener{
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }

    }

    private fun loadData(): Pair<String?, Boolean?> {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val myUID = sharedPref?.getString("myUID", null).toString()
        val state = sharedPref?.getBoolean("state", false)
        return Pair(myUID, state)
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
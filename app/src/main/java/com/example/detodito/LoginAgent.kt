package com.example.detodito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.detodito.databinding.ActivityLoginAgentBinding
import com.example.detodito.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LoginAgent : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAgentBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()


        val (myUID, state, typeSesion) = loadData()

        binding.register.setOnClickListener{
            val intent = Intent(applicationContext, AgentSignup::class.java)
            startActivity(intent)
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
                                            shared(idToken, true, "Agente")
                                        }
                                        val intent = Intent(applicationContext, MainActivity::class.java)
                                        intent.putExtra("token", idToken)
                                        dialog.dismiss()
                                        startActivity(intent)
                                        finish()
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

    }

    private fun loadData(): Triple<String?, Boolean?, String?> {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val myUID = sharedPref?.getString("myUID", null)
        val state = sharedPref?.getBoolean("state", false)
        val type = sharedPref?.getString("type", null)
        return Triple(myUID, state, type)
    }

    private fun shared(myUID: String, state: Boolean, type:String) {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("myUID", myUID)
            putBoolean("state", state)
            putString("type", type)
            apply()
        }
    }
}
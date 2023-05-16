package com.example.detodito

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.detodito.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var auth: FirebaseAuth
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor =
                Color.parseColor("#FFFFFF")
        }

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()


        val (myUID, state, typeSesion) = loadData()

        if (state == true) {
            dialog.show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("myUID", myUID)
            dialog.dismiss()
            startActivity(intent)
            finish()
        }

        binding.loginButton.setOnClickListener {
            login("test@example.com", "password", "Nada")
        }

        binding.register.setOnClickListener{
            val intent = Intent(applicationContext, Signup::class.java)
            startActivity(intent)
        }

        binding.agent.setOnClickListener {
            val intent = Intent(applicationContext, LoginAgent::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun login(s: String, s1: String, s2: String) {
        if (s2 == "Prueba") {
            // Usar credenciales de prueba
            val email = s
            val password = s1
            signIn(email, password)
        } else {
            // Obtener credenciales de los campos de correo electrónico y contraseña
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()

            // Verificar que el correo electrónico y la contraseña no estén vacíos
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                // Mostrar errores si las entradas están vacías
                if (password.isEmpty()) {
                    binding.passwordLayout.error = "Ingresa una contraseña"
                }
                if (email.isEmpty()) {
                    binding.emailLayout.error = "Ingresa un correo valido"
                }
            }
        }
    }

    private fun signIn(email: String, password: String) {
        try {
            // Iniciar sesión con correo electrónico y contraseña
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        // Obtener el token de autenticación
                        user?.getIdToken(true)?.addOnCompleteListener { task ->
                            val idToken = task?.result?.token
                            if (idToken != null) {
                                // Guardar el token de autenticación
                                shared(idToken, true, "Cliente")
                            }
                            // Ir a la actividad principal con el token de autenticación
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.putExtra("token", idToken)
                            startActivity(intent)
                            Toast.makeText(
                                baseContext, "Authentication Successful.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
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

    private fun loadData(): Triple<String?, Boolean?, String?> {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val myUID = sharedPref?.getString("myUID", null)
        val state = sharedPref?.getBoolean("state", false)
        val type = sharedPref?.getString("type", null)
        return Triple(myUID, state, type)
    }

    fun shared(myUID: String, state: Boolean, type:String) {
        val sharedPref = applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("myUID", myUID)
            putBoolean("state", state)
            putString("type", type)
            apply()
        }
    }
}
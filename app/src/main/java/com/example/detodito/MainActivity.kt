package com.example.detodito

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.detodito.data.UserData
import com.example.detodito.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor =
                Color.parseColor("#FFFFFF") // Reemplaza #FF0000 con el color deseado
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.customButton.setOnClickListener {
            val intent = Intent(applicationContext, UserDataV::class.java)
            startActivity(intent)
        }

        val (myUID, state) = loadData()

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

                try {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val userData = gson.fromJson(responseData, UserData::class.java)
                    val refresh = Handler(Looper.getMainLooper())
                    refresh.post(kotlinx.coroutines.Runnable {
                        binding.textName.text = userData.userData.firstName
                    })
                } catch (e: Exception) {
                    val refresh = Handler(Looper.getMainLooper())
                    refresh.post(kotlinx.coroutines.Runnable {
                        val intent = Intent(applicationContext, Login::class.java)
                        shared("", false)
                        startActivity(intent)
                        finish()
                    })
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Infla el menú para esta actividad
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Maneja los elementos de menú seleccionados
        return when (item.itemId) {
            R.id.action_custom_button -> {
                val intent = Intent(applicationContext, Login::class.java)
                shared("", false)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun loadData(): Pair<String?, Boolean?> {
        val sharedPref =
            applicationContext.getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val myUID = sharedPref?.getString("myUID", null).toString()
        val state = sharedPref?.getBoolean("state", false)
        return Pair(myUID, state)
    }

}
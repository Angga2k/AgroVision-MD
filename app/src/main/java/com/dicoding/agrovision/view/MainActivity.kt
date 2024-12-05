package com.dicoding.agrovision.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.AgroVision.R
import com.dicoding.AgroVision.databinding.ActivityMainBinding
import com.dicoding.agrovision.view.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        if (auth.currentUser == null) {
            // If no user is logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity to prevent back navigation
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Handle Toolbar Icon Click
        binding.iconPerson.setOnClickListener {
            Toast.makeText(this, "Profil akan ditampilkan!", Toast.LENGTH_SHORT).show()
        }

        // Handle Button Clicks
        binding.albumButton.setOnClickListener {
            Toast.makeText(this, "Buka album...", Toast.LENGTH_SHORT).show()
            // Intent untuk membuka galeri atau album
        }

        binding.cameraButton.setOnClickListener {
            Toast.makeText(this, "Buka kamera...", Toast.LENGTH_SHORT).show()
            // Intent untuk membuka kamera
        }

        binding.checkButton.setOnClickListener {
            Toast.makeText(this, "Memulai pemeriksaan...", Toast.LENGTH_SHORT).show()
            // Logic untuk fitur pemeriksaan
        }

        // Setup Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = binding.bottomNavigationView

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.news -> {
                    Toast.makeText(this, "News selected", Toast.LENGTH_SHORT).show()
                    // Logic untuk pindah ke halaman scan
                    true
                }
                R.id.history -> {
                    Toast.makeText(this, "History selected", Toast.LENGTH_SHORT).show()
                    // Logic untuk pindah ke halaman profil
                    true
                }
                else -> false
            }
        }
    }
}

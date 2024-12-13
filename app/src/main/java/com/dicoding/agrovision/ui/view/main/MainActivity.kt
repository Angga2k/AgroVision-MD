package com.dicoding.agrovision.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.ui.view.news.NewsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.widget.Toolbar
import com.dicoding.agrovision.ui.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.dicoding.agrovision.data.local.UserPreference
import com.dicoding.agrovision.ui.view.history.HistoryFragment
import com.dicoding.agrovision.ui.view.profile.ProfileActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val userPreference = UserPreference(applicationContext)

        val token = runBlocking { userPreference.getToken().first() }

        if (token.isNullOrEmpty() || auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val imageViewProfile: ImageView = toolbar.findViewById(R.id.imageViewProfile)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Jika savedInstanceState null, ganti fragment dengan HomeFragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Setup BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.news -> {
                    replaceFragment(NewsFragment())
                    true
                }
                R.id.history -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                else -> false
            }
        }
        imageViewProfile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        Log.d("MainActivity", "Replacing fragment: ${fragment::class.java.simpleName}")
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}

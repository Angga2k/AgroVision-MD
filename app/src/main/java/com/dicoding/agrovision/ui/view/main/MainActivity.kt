package com.dicoding.agrovision.ui.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.ui.view.main.HomeFragment
import com.dicoding.agrovision.ui.view.news.NewsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Menyiapkan Fragment pertama yang akan ditampilkan (misalnya HomeFragment)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Setup BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Ganti fragment ke HomeFragment
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.news -> {
                    // Ganti fragment ke NewsFragment
                    replaceFragment(NewsFragment())
                    true
                }
                R.id.history -> {

                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()
    }
}

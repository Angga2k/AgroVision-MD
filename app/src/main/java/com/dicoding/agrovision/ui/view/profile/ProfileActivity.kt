package com.dicoding.agrovision.ui.view.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.ui.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Views
        val emailField: EditText = findViewById(R.id.inputField)
        val passwordField: EditText = findViewById(R.id.inputField2)
        val usernameField: EditText = findViewById(R.id.inputField4)
        val profileImage: ImageView = findViewById(R.id.profileImage)
        val logoutButton: Button = findViewById(R.id.button2)

        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        if (currentUser != null) {
            // Retrieve user data
            val email = currentUser.email
            val displayName = currentUser.displayName ?: "User"

            // Populate fields
            emailField.setText(email)
            usernameField.setText(displayName)
            passwordField.setText("******")
        } else {
            // Handle null user (e.g., session expired)
            Toast.makeText(this, "User not logged in. Redirecting to login screen.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

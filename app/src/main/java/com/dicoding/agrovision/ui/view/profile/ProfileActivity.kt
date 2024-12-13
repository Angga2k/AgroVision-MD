package com.dicoding.agrovision.ui.view.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.ui.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Views
        val emailField: EditText = findViewById(R.id.inputField)
        val passwordField: EditText = findViewById(R.id.inputField2)
        val usernameField: EditText = findViewById(R.id.inputField4)
        val logoutButton: Button = findViewById(R.id.button2)

        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        if (currentUser != null) {
            // Retrieve user data from Firestore
            val userId = currentUser.uid
            val userRef = firestore.collection("users").document(userId)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Retrieve user data from Firestore
                    val email = currentUser.email
                    val displayName = documentSnapshot.getString("name") ?: currentUser.displayName

                    // Populate fields
                    emailField.setText(email)
                    usernameField.setText(displayName)
                    passwordField.setText("******")
                } else {
                    // Handle case where user data does not exist in Firestore
                    Toast.makeText(this, "User profile not found in Firestore.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching profile data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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

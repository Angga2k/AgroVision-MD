package com.dicoding.agrovision.ui.view.register

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.AgroVision.R
import com.dicoding.agrovision.data.model.User
import com.dicoding.agrovision.ui.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        editTextName = findViewById(R.id.username)
        editTextEmail = findViewById(R.id.etEmail)
        editTextPassword = findViewById(R.id.etPassword)
        buttonRegister = findViewById(R.id.btnSignUp)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim() // Ambil nama
            val email = editTextEmail.text.toString().trim() // Ambil email
            val password = editTextPassword.text.toString().trim() // Ambil password

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()



                            val userId = user?.uid
                            val userObj = User(name, email)

                            // Save user data to Firestore
                            if (userId != null) {
                                firestore.collection("users").document(userId)
                                    .set(userObj)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Data pengguna berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, "Gagal menyimpan data pengguna: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Pendaftaran gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Nama, email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

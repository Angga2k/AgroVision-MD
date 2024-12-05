package com.dicoding.agrovision.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.AgroVision.R
import com.dicoding.AgroVision.databinding.ActivityRegisterBinding
import com.dicoding.agrovision.data.model.User
import com.dicoding.agrovision.view.MainActivity
import com.dicoding.agrovision.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
                            val database = FirebaseDatabase.getInstance().getReference("Users")
                            val userObj = User(name, email)

                            if (userId != null) {
                                database.child(userId).setValue(userObj).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Data pengguna berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                                    }
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

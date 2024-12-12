package com.dicoding.agrovision.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.AgroVision.databinding.ActivityLoginBinding
import com.dicoding.agrovision.data.local.UserPreference
import com.dicoding.agrovision.ui.view.main.MainActivity
import com.dicoding.agrovision.ui.view.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        userPreference = UserPreference(this) // Inisialisasi UserPreference

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email harus diisi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email Tidak Valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error = "Password min 6 Karakter"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            loginFirebase(email, password)
        }
    }

    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        val token = result.token
                        if (!token.isNullOrEmpty()) {
                            // Simpan token ke UserPreference
                            lifecycleScope.launch {
                                userPreference.saveToken(token)
                                Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

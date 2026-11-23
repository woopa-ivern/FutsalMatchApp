package com.futsalmatch.app.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.BaseActivity
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.ActivityLoginBinding
import com.futsalmatch.app.util.SecurityUtil
import com.futsalmatch.app.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        binding.btnMasuk.setOnClickListener {
            attemptLogin()
        }

        binding.btnLanjutTamu.setOnClickListener {
            SessionManager.clearSession(this)
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.tvDaftarTim.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        binding.tilUsername.error = null
        binding.tilPassword.error = null

        if (username.isEmpty()) {
            binding.tilUsername.error = "Username tidak boleh kosong."
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong."
            return
        }

        binding.btnMasuk.isEnabled = false
        lifecycleScope.launch {
            val user = db.userDao().findByUsername(username)
            val passwordHash = SecurityUtil.sha256(password)

            if (user != null && user.passwordHash == passwordHash) {
                SessionManager.saveSession(
                    context = this@LoginActivity,
                    userId = user.id,
                    teamId = user.teamId,
                    username = user.username
                )
                val intent = Intent(this@LoginActivity, BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                binding.btnMasuk.isEnabled = true
                MaterialAlertDialogBuilder(this@LoginActivity)
                    .setTitle("Login gagal")
                    .setMessage("Username atau password salah. Periksa kembali akun kamu atau daftar tim baru.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}

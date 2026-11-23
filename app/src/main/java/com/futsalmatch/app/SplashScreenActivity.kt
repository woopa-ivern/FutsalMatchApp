package com.futsalmatch.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.DatabaseSeeder
import com.futsalmatch.app.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(this@SplashScreenActivity)
            DatabaseSeeder.seedIfEmpty(db)
            delay(1200)
            startActivity(Intent(this@SplashScreenActivity, BaseActivity::class.java))
            finish()
        }
    }
}

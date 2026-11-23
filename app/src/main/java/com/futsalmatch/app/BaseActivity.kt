package com.futsalmatch.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.futsalmatch.app.auth.LoginActivity
import com.futsalmatch.app.data.SkillLevel
import com.futsalmatch.app.databinding.ActivityBaseBinding
import com.futsalmatch.app.ui.home.HomeFragment
import com.futsalmatch.app.ui.match.RiwayatFragment
import com.futsalmatch.app.ui.profile.ProfilTimSayaFragment
import com.futsalmatch.app.ui.team.CariLawanFragment
import com.futsalmatch.app.ui.team.DaftarTimFragment
import com.futsalmatch.app.util.SessionManager

class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding
    private var pendingSkillFilter: SkillLevel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val isMember = SessionManager.isLoggedIn(this)
        binding.bottomNavView.menu.clear()
        if (isMember) {
            binding.bottomNavView.inflateMenu(R.menu.bottom_nav_member)
        } else {
            binding.bottomNavView.inflateMenu(R.menu.bottom_nav_guest)
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_tim -> {
                    replaceFragment(DaftarTimFragment())
                    true
                }
                R.id.nav_cari -> {
                    val filter = pendingSkillFilter
                    pendingSkillFilter = null
                    replaceFragment(CariLawanFragment.newInstance(filter))
                    true
                }
                R.id.nav_riwayat -> {
                    if (SessionManager.isLoggedIn(this)) {
                        replaceFragment(RiwayatFragment())
                        true
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        false
                    }
                }
                R.id.nav_profil -> {
                    if (SessionManager.isLoggedIn(this)) {
                        replaceFragment(ProfilTimSayaFragment())
                        true
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        false
                    }
                }
                R.id.nav_masuk -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    fun openTab(menuItemId: Int) {
        binding.bottomNavView.selectedItemId = menuItemId
    }

    fun openCariLawanWithSkill(skill: SkillLevel?) {
        pendingSkillFilter = skill
        binding.bottomNavView.selectedItemId = R.id.nav_cari
    }
}

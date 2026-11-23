package com.futsalmatch.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.futsalmatch.app.BaseActivity
import com.futsalmatch.app.R
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.SkillLevel
import com.futsalmatch.app.data.entity.PlayerEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.data.entity.UserEntity
import com.futsalmatch.app.databinding.ActivityRegisterBinding
import com.futsalmatch.app.util.ImageStorageUtil
import com.futsalmatch.app.util.SecurityUtil
import com.futsalmatch.app.util.SessionManager
import kotlinx.coroutines.launch
import java.io.File

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase

    private var currentStep = 1
    private var selectedPhotoPath: String? = null
    private val playerRowViews = mutableListOf<View>()

    private val kotaOptions = listOf(
        "Pekanbaru", "Surabaya", "Malang", "Jakarta", "Bandung",
        "Medan", "Yogyakarta", "Semarang", "Makassar", "Palembang"
    )

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val savedPath = ImageStorageUtil.copyUriToFilesDir(this, uri)
            if (savedPath != null) {
                selectedPhotoPath = savedPath
                binding.ivPreviewFoto.imageTintList = null
                Glide.with(this)
                    .load(File(savedPath))
                    .circleCrop()
                    .into(binding.ivPreviewFoto)
                binding.tvUploadTitle.text = "Foto tim dipilih"
                binding.tvUploadSubtitle.text = "Ketuk lagi untuk mengganti"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        binding.toolbar.setNavigationOnClickListener {
            handleBackStep()
        }

        val kotaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kotaOptions)
        binding.actvKota.setAdapter(kotaAdapter)

        repeat(5) { index ->
            addPlayerInputRow(index + 1)
        }

        binding.btnUploadFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnNextToStep2.setOnClickListener {
            validateStep1()
        }

        binding.btnNextToStep3.setOnClickListener {
            validateStep2()
        }

        binding.btnAddPlayerRow.setOnClickListener {
            addPlayerInputRow(playerRowViews.size + 1)
        }

        binding.btnFinishRegister.setOnClickListener {
            validateAndSubmit()
        }

        updateStepUI()
    }

    private fun handleBackStep() {
        when (currentStep) {
            1 -> finish()
            2 -> {
                currentStep = 1
                updateStepUI()
            }
            3 -> {
                currentStep = 2
                updateStepUI()
            }
        }
    }

    private fun addPlayerInputRow(number: Int) {
        val row = LayoutInflater.from(this)
            .inflate(R.layout.item_player_input, binding.containerPlayers, false)
        val etName = row.findViewById<EditText>(R.id.etPlayerName)
        etName.hint = "Pemain $number"
        binding.containerPlayers.addView(row)
        playerRowViews.add(row)
    }

    private fun updateStepUI() {
        binding.layoutStep1.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        binding.layoutStep2.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        binding.layoutStep3.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        binding.tvStep1Circle.setBackgroundResource(
            if (currentStep >= 1) R.drawable.bg_step_active else R.drawable.bg_step_inactive
        )
        binding.tvStep1Circle.setTextColor(getColor(R.color.putih))
        binding.tvStep1Circle.text = if (currentStep > 1) "✓" else "1"

        binding.lineStep12.setBackgroundColor(
            getColor(if (currentStep >= 2) R.color.lapangan else R.color.garis)
        )

        binding.tvStep2Circle.setBackgroundResource(
            if (currentStep >= 2) R.drawable.bg_step_active else R.drawable.bg_step_inactive
        )
        binding.tvStep2Circle.setTextColor(
            getColor(if (currentStep >= 2) R.color.putih else R.color.abu_teks)
        )
        binding.tvStep2Circle.text = if (currentStep > 2) "✓" else "2"

        binding.lineStep23.setBackgroundColor(
            getColor(if (currentStep >= 3) R.color.lapangan else R.color.garis)
        )

        binding.tvStep3Circle.setBackgroundResource(
            if (currentStep >= 3) R.drawable.bg_step_active else R.drawable.bg_step_inactive
        )
        binding.tvStep3Circle.setTextColor(
            getColor(if (currentStep >= 3) R.color.putih else R.color.abu_teks)
        )
    }

    private fun validateStep1() {
        val username = binding.etRegUsername.text.toString().trim()
        val password = binding.etRegPassword.text.toString()
        val confirm = binding.etRegConfirmPassword.text.toString()

        binding.tilRegUsername.error = null
        binding.tilRegPassword.error = null
        binding.tilRegConfirmPassword.error = null

        if (username.length < 3) {
            binding.tilRegUsername.error = "Username minimal 3 karakter."
            return
        }
        if (username.contains(" ")) {
            binding.tilRegUsername.error = "Username tidak boleh mengandung spasi."
            return
        }
        if (password.length < 6) {
            binding.tilRegPassword.error = "Password minimal 6 karakter."
            return
        }
        if (password != confirm) {
            binding.tilRegConfirmPassword.error = "Konfirmasi password tidak cocok."
            return
        }

        lifecycleScope.launch {
            val existing = db.userDao().findByUsername(username)
            if (existing != null) {
                binding.tilRegUsername.error = "Username sudah dipakai. Coba username lain."
            } else {
                currentStep = 2
                updateStepUI()
            }
        }
    }

    private fun validateStep2() {
        val namaTim = binding.etNamaTim.text.toString().trim()
        val kota = binding.actvKota.text.toString().trim()
        val noWa = binding.etNoWa.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()

        binding.tilNamaTim.error = null
        binding.tilKota.error = null
        binding.tilNoWa.error = null
        binding.tilDeskripsi.error = null

        if (namaTim.isEmpty()) {
            binding.tilNamaTim.error = "Nama tim wajib diisi."
            return
        }
        if (kota.isEmpty()) {
            binding.tilKota.error = "Kota wajib diisi."
            return
        }
        if (noWa.filter { it.isDigit() }.length < 9) {
            binding.tilNoWa.error = "Masukkan nomor WhatsApp yang valid."
            return
        }
        if (deskripsi.isEmpty()) {
            binding.tilDeskripsi.error = "Deskripsi singkat tim wajib diisi."
            return
        }

        lifecycleScope.launch {
            val existingTeam = db.teamDao().findByNama(namaTim)
            if (existingTeam != null) {
                binding.tilNamaTim.error = "Nama tim sudah dipakai. Coba nama lain."
            } else {
                currentStep = 3
                updateStepUI()
            }
        }
    }

    private fun getSelectedSkill(): SkillLevel {
        return when (binding.toggleSkill.checkedButtonId) {
            R.id.btnSkillJago -> SkillLevel.JAGO
            R.id.btnSkillPemula -> SkillLevel.PEMULA
            else -> SkillLevel.SEDANG
        }
    }

    private fun validateAndSubmit() {
        val players = mutableListOf<Pair<String, String?>>()
        for (row in playerRowViews) {
            val name = row.findViewById<EditText>(R.id.etPlayerName).text.toString().trim()
            val pos = row.findViewById<EditText>(R.id.etPlayerPosition).text.toString().trim()
            if (name.isNotEmpty()) {
                players.add(name to pos.ifEmpty { null })
            }
        }

        if (players.size < 5) {
            Toast.makeText(this, "Minimal 5 nama pemain wajib diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        val username = binding.etRegUsername.text.toString().trim()
        val password = binding.etRegPassword.text.toString()
        val namaTim = binding.etNamaTim.text.toString().trim()
        val kota = binding.actvKota.text.toString().trim()
        val noWa = binding.etNoWa.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val skill = getSelectedSkill()

        binding.btnFinishRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val teamId = db.teamDao().insert(
                    TeamEntity(
                        namaTim = namaTim,
                        kota = kota,
                        skillLevel = skill,
                        noWhatsapp = noWa,
                        deskripsiSingkat = deskripsi,
                        fotoPath = selectedPhotoPath
                    )
                ).toInt()

                val userId = db.userDao().insert(
                    UserEntity(
                        username = username,
                        passwordHash = SecurityUtil.sha256(password),
                        teamId = teamId
                    )
                ).toInt()

                val playerEntities = players.map { (name, pos) ->
                    PlayerEntity(
                        teamId = teamId,
                        namaPemain = name,
                        posisi = pos
                    )
                }
                db.playerDao().insertAll(playerEntities)

                SessionManager.saveSession(this@RegisterActivity, userId, teamId, username)

                Toast.makeText(this@RegisterActivity, "Tim $namaTim berhasil didaftarkan!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                binding.btnFinishRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Gagal mendaftarkan tim: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

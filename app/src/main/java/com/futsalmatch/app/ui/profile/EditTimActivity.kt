package com.futsalmatch.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.R
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.SkillLevel
import com.futsalmatch.app.data.entity.PlayerEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.databinding.ActivityEditTimBinding
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.ImageStorageUtil
import com.futsalmatch.app.util.SessionManager
import kotlinx.coroutines.launch

class EditTimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTimBinding
    private lateinit var db: AppDatabase

    private var currentTeam: TeamEntity? = null
    private var selectedPhotoPath: String? = null
    private val playerRowViews = mutableListOf<View>()
    private var playersExpanded = false

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
                val team = currentTeam
                if (team != null) {
                    AvatarHelper.bindTeamAvatar(
                        this,
                        team.id,
                        team.namaTim,
                        selectedPhotoPath,
                        binding.ivEditAvatar,
                        binding.tvEditInitials
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val kotaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kotaOptions)
        binding.actvEditKota.setAdapter(kotaAdapter)

        binding.btnGantiFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnToggleEditPlayers.setOnClickListener {
            playersExpanded = !playersExpanded
            binding.layoutEditPlayersWrapper.visibility =
                if (playersExpanded) View.VISIBLE else View.GONE
        }

        binding.btnEditAddPlayer.setOnClickListener {
            addPlayerRow("", "")
        }

        val saveAction = View.OnClickListener { saveChanges() }
        binding.tvSimpanTop.setOnClickListener(saveAction)
        binding.btnSimpanEditTim.setOnClickListener(saveAction)

        loadTeam()
    }

    private fun addPlayerRow(name: String, position: String) {
        val row = LayoutInflater.from(this)
            .inflate(R.layout.item_player_input, binding.containerEditPlayers, false)
        row.findViewById<EditText>(R.id.etPlayerName).setText(name)
        row.findViewById<EditText>(R.id.etPlayerPosition).setText(position)
        binding.containerEditPlayers.addView(row)
        playerRowViews.add(row)
    }

    private fun loadTeam() {
        val myTeamId = SessionManager.getTeamId(this)
        lifecycleScope.launch {
            val team = db.teamDao().getById(myTeamId) ?: return@launch
            val players = db.playerDao().getByTeamId(myTeamId)

            currentTeam = team
            selectedPhotoPath = team.fotoPath

            AvatarHelper.bindTeamAvatar(
                this@EditTimActivity,
                team.id,
                team.namaTim,
                team.fotoPath,
                binding.ivEditAvatar,
                binding.tvEditInitials
            )

            binding.etEditNamaTim.setText(team.namaTim)
            binding.actvEditKota.setText(team.kota, false)
            binding.etEditWa.setText(team.noWhatsapp)
            binding.etEditDeskripsi.setText(team.deskripsiSingkat)

            val checkId = when (team.skillLevel) {
                SkillLevel.JAGO -> R.id.btnEditSkillJago
                SkillLevel.PEMULA -> R.id.btnEditSkillPemula
                SkillLevel.SEDANG -> R.id.btnEditSkillSedang
            }
            binding.toggleEditSkill.check(checkId)

            binding.tvEditAnggotaLabel.text = "Ubah anggota (${players.size})"
            binding.containerEditPlayers.removeAllViews()
            playerRowViews.clear()
            for (p in players) {
                addPlayerRow(p.namaPemain, p.posisi ?: "")
            }
        }
    }

    private fun getSelectedSkill(): SkillLevel {
        return when (binding.toggleEditSkill.checkedButtonId) {
            R.id.btnEditSkillJago -> SkillLevel.JAGO
            R.id.btnEditSkillPemula -> SkillLevel.PEMULA
            else -> SkillLevel.SEDANG
        }
    }

    private fun saveChanges() {
        val team = currentTeam ?: return
        val namaTim = binding.etEditNamaTim.text.toString().trim()
        val kota = binding.actvEditKota.text.toString().trim()
        val noWa = binding.etEditWa.text.toString().trim()
        val deskripsi = binding.etEditDeskripsi.text.toString().trim()
        val skill = getSelectedSkill()

        binding.tilEditNamaTim.error = null
        binding.tilEditKota.error = null
        binding.tilEditWa.error = null

        if (namaTim.isEmpty()) {
            binding.tilEditNamaTim.error = "Nama tim wajib diisi."
            return
        }
        if (kota.isEmpty()) {
            binding.tilEditKota.error = "Kota wajib diisi."
            return
        }
        if (noWa.filter { it.isDigit() }.length < 9) {
            binding.tilEditWa.error = "Masukkan nomor WhatsApp yang valid."
            return
        }

        val players = mutableListOf<Pair<String, String?>>()
        for (row in playerRowViews) {
            val name = row.findViewById<EditText>(R.id.etPlayerName).text.toString().trim()
            val pos = row.findViewById<EditText>(R.id.etPlayerPosition).text.toString().trim()
            if (name.isNotEmpty()) {
                players.add(name to pos.ifEmpty { null })
            }
        }

        if (players.size < 5) {
            Toast.makeText(this, "Minimal 5 nama pemain harus terisi.", Toast.LENGTH_SHORT).show()
            playersExpanded = true
            binding.layoutEditPlayersWrapper.visibility = View.VISIBLE
            return
        }

        binding.btnSimpanEditTim.isEnabled = false
        lifecycleScope.launch {
            val duplicate = db.teamDao().findByNama(namaTim)
            if (duplicate != null && duplicate.id != team.id) {
                binding.btnSimpanEditTim.isEnabled = true
                binding.tilEditNamaTim.error = "Nama tim sudah dipakai. Coba nama lain."
                return@launch
            }

            val updated = team.copy(
                namaTim = namaTim,
                kota = kota,
                skillLevel = skill,
                noWhatsapp = noWa,
                deskripsiSingkat = deskripsi,
                fotoPath = selectedPhotoPath
            )
            db.teamDao().update(updated)
            db.playerDao().deleteByTeamId(team.id)
            db.playerDao().insertAll(
                players.map { (name, pos) ->
                    PlayerEntity(teamId = team.id, namaPemain = name, posisi = pos)
                }
            )

            Toast.makeText(this@EditTimActivity, "Perubahan tim disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

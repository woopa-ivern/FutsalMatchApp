package com.futsalmatch.app.ui.match

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.databinding.ActivityTambahMatchBinding
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import kotlinx.coroutines.launch
import java.util.Calendar

class TambahMatchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahMatchBinding
    private lateinit var db: AppDatabase

    private var myTeam: TeamEntity? = null
    private var opponents: List<TeamEntity> = emptyList()
    private var selectedOpponent: TeamEntity? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvTanggalTerpilih.text = FormatUtil.formatDateFull(selectedDateMillis)
        binding.btnPilihTanggal.setOnClickListener { showDatePicker() }
        binding.btnSimpanMatch.setOnClickListener { saveMatch() }

        loadTeams()
    }

    private fun loadTeams() {
        val myTeamId = SessionManager.getTeamId(this)
        lifecycleScope.launch {
            myTeam = db.teamDao().getById(myTeamId)
            val allTeams = db.teamDao().getAll()
            opponents = allTeams.filter { it.id != myTeamId }

            binding.tvSkorHomeLabel.text = myTeam?.namaTim ?: "Tim Saya"

            val names = opponents.map { "${it.namaTim} (${it.kota} · ${it.skillLevel.label})" }
            val adapter = ArrayAdapter(
                this@TambahMatchActivity,
                android.R.layout.simple_spinner_item,
                names
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spinnerTimLawan.adapter = adapter
            binding.spinnerTimLawan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position in opponents.indices) {
                        selectedOpponent = opponents[position]
                        binding.tvSkorAwayLabel.text = selectedOpponent?.namaTim ?: "Tim Lawan"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance()
                picked.set(year, month, dayOfMonth, 19, 0, 0)
                selectedDateMillis = picked.timeInMillis
                binding.tvTanggalTerpilih.text = FormatUtil.formatDateFull(selectedDateMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveMatch() {
        val home = myTeam
        val away = selectedOpponent
        if (home == null || away == null) {
            Toast.makeText(this, "Pilih tim lawan terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }

        val skorHomeStr = binding.etSkorHome.text.toString().trim()
        val skorAwayStr = binding.etSkorAway.text.toString().trim()
        val lokasi = binding.etLokasiMatch.text.toString().trim()

        binding.tilLokasiMatch.error = null

        val skorHome = skorHomeStr.toIntOrNull()
        val skorAway = skorAwayStr.toIntOrNull()
        if (skorHome == null || skorAway == null) {
            Toast.makeText(this, "Isi skor kedua tim terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }
        if (lokasi.isEmpty()) {
            binding.tilLokasiMatch.error = "Lokasi lapangan wajib diisi."
            return
        }

        binding.btnSimpanMatch.isEnabled = false
        lifecycleScope.launch {
            db.matchDao().insert(
                MatchEntity(
                    teamHomeId = home.id,
                    teamAwayId = away.id,
                    skorHome = skorHome,
                    skorAway = skorAway,
                    tanggal = selectedDateMillis,
                    lokasi = lokasi,
                    sudahDiReview = false
                )
            )
            Toast.makeText(this@TambahMatchActivity, "Match berhasil dicatat!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

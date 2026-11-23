package com.futsalmatch.app.ui.match

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.ReviewEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.databinding.ActivityFormReviewBinding
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class FormReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormReviewBinding
    private lateinit var db: AppDatabase

    private var matchId: Int = -1
    private var currentMatch: MatchEntity? = null
    private var opponentTeam: TeamEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        matchId = intent.getIntExtra(EXTRA_MATCH_ID, -1)

        binding.toolbar.setNavigationOnClickListener { finish() }

        if (matchId == -1 || !SessionManager.isLoggedIn(this)) {
            finish()
            return
        }

        binding.rbSportivitas.setOnRatingBarChangeListener { _, rating, _ ->
            val value = rating.toInt().coerceAtLeast(1)
            if (rating < 1f) binding.rbSportivitas.rating = 1f
            binding.tvRatingSportivitas.text = "$value/5"
        }

        binding.rbKetepatan.setOnRatingBarChangeListener { _, rating, _ ->
            val value = rating.toInt().coerceAtLeast(1)
            if (rating < 1f) binding.rbKetepatan.rating = 1f
            binding.tvRatingKetepatan.text = "$value/5"
        }

        binding.rbSkill.setOnRatingBarChangeListener { _, rating, _ ->
            val value = rating.toInt().coerceAtLeast(1)
            if (rating < 1f) binding.rbSkill.rating = 1f
            binding.tvRatingSkill.text = "$value/5"
        }

        binding.btnKirimReview.setOnClickListener {
            confirmAndSubmit()
        }

        loadContext()
    }

    private fun loadContext() {
        val myTeamId = SessionManager.getTeamId(this)
        lifecycleScope.launch {
            val match = db.matchDao().getById(matchId) ?: run {
                finish()
                return@launch
            }
            val homeTeam = db.teamDao().getById(match.teamHomeId) ?: return@launch
            val awayTeam = db.teamDao().getById(match.teamAwayId) ?: return@launch

            val isParticipant = myTeamId == homeTeam.id || myTeamId == awayTeam.id
            if (!isParticipant || match.sudahDiReview || db.reviewDao().getByMatchId(match.id) != null) {
                Toast.makeText(this@FormReviewActivity, "Review tidak tersedia atau sudah terkunci.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            currentMatch = match
            val target = if (myTeamId == homeTeam.id) awayTeam else homeTeam
            opponentTeam = target

            AvatarHelper.bindTeamAvatar(
                this@FormReviewActivity,
                target.id,
                target.namaTim,
                target.fotoPath,
                binding.ivTargetAvatar,
                binding.tvTargetInitials
            )
            binding.tvTargetTeamName.text = target.namaTim
            binding.tvTargetMatchInfo.text =
                "${homeTeam.namaTim} ${match.skorHome} - ${match.skorAway} ${awayTeam.namaTim} · ${FormatUtil.formatDateShort(match.tanggal)}"
        }
    }

    private fun confirmAndSubmit() {
        val targetName = opponentTeam?.namaTim ?: "tim lawan"
        val catatan = binding.etCatatanReview.text.toString().trim()
        if (catatan.isEmpty()) {
            binding.tilCatatanReview.error = "Tuliskan catatan singkat tentang pertandingan."
            return
        }
        binding.tilCatatanReview.error = null

        MaterialAlertDialogBuilder(this)
            .setTitle("Kirim review sekarang?")
            .setMessage("Review untuk $targetName tidak bisa diubah setelah dikirim.")
            .setNegativeButton("Periksa lagi", null)
            .setPositiveButton("Kirim") { _, _ ->
                submitReview()
            }
            .show()
    }

    private fun submitReview() {
        val match = currentMatch ?: return
        val target = opponentTeam ?: return
        val myTeamId = SessionManager.getTeamId(this)

        // 4 Blueprint Validations before saving review
        val isParticipant = myTeamId == match.teamHomeId || myTeamId == match.teamAwayId
        val isNotSelfReview = myTeamId != target.id
        val scoreFilled = match.skorHome >= 0 && match.skorAway >= 0

        if (!isParticipant || !isNotSelfReview || !scoreFilled) {
            Toast.makeText(this, "Validasi review gagal.", Toast.LENGTH_SHORT).show()
            return
        }

        val rSport = binding.rbSportivitas.rating.toInt().coerceIn(1, 5)
        val rTime = binding.rbKetepatan.rating.toInt().coerceIn(1, 5)
        val rSkill = binding.rbSkill.rating.toInt().coerceIn(1, 5)
        val catatan = binding.etCatatanReview.text.toString().trim()

        binding.btnKirimReview.isEnabled = false
        lifecycleScope.launch {
            val existing = db.reviewDao().getByMatchId(match.id)
            if (existing != null || match.sudahDiReview) {
                Toast.makeText(this@FormReviewActivity, "Review sudah terkunci sebelumnya.", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            db.reviewDao().insert(
                ReviewEntity(
                    matchId = match.id,
                    reviewerTeamId = myTeamId,
                    ratingSportivitas = rSport,
                    ratingKetepatanWaktu = rTime,
                    ratingSkillMatch = rSkill,
                    catatanTeks = catatan,
                    tanggalSubmit = System.currentTimeMillis(),
                    isLocked = true
                )
            )
            db.matchDao().markReviewed(match.id)

            Toast.makeText(this@FormReviewActivity, "Review terkirim dan terkunci!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_MATCH_ID = "extra_match_id"
    }
}

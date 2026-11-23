package com.futsalmatch.app.ui.match

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.ActivityDetailMatchBinding
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import kotlinx.coroutines.launch

class DetailMatchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMatchBinding
    private lateinit var db: AppDatabase
    private var matchId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        matchId = intent.getIntExtra(EXTRA_MATCH_ID, -1)

        binding.toolbar.setNavigationOnClickListener { finish() }

        if (matchId == -1) {
            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        loadMatchDetail()
    }

    private fun renderStars(count: Int): String {
        val filled = "★".repeat(count.coerceIn(0, 5))
        val empty = "☆".repeat((5 - count).coerceIn(0, 5))
        return "$filled$empty ($count/5)"
    }

    private fun loadMatchDetail() {
        val myTeamId = SessionManager.getTeamId(this)
        lifecycleScope.launch {
            val match = db.matchDao().getById(matchId) ?: return@launch
            val homeTeam = db.teamDao().getById(match.teamHomeId) ?: return@launch
            val awayTeam = db.teamDao().getById(match.teamAwayId) ?: return@launch
            val review = db.reviewDao().getByMatchId(match.id)

            AvatarHelper.bindTeamAvatar(
                this@DetailMatchActivity,
                homeTeam.id,
                homeTeam.namaTim,
                homeTeam.fotoPath,
                binding.ivHomeAvatar,
                binding.tvHomeInitials
            )
            AvatarHelper.bindTeamAvatar(
                this@DetailMatchActivity,
                awayTeam.id,
                awayTeam.namaTim,
                awayTeam.fotoPath,
                binding.ivAwayAvatar,
                binding.tvAwayInitials
            )

            binding.tvHomeName.text = homeTeam.namaTim
            binding.tvAwayName.text = awayTeam.namaTim
            binding.tvScoreBig.text = "${match.skorHome} - ${match.skorAway}"
            binding.tvDetailMatchDate.text = FormatUtil.formatDateWithDay(match.tanggal)
            binding.tvDetailMatchLocation.text = match.lokasi

            val isParticipant = myTeamId == homeTeam.id || myTeamId == awayTeam.id
            val opponent = if (myTeamId == homeTeam.id) awayTeam else homeTeam

            if (review != null) {
                binding.cardBelumReview.visibility = View.GONE
                binding.cardSudahReview.visibility = View.VISIBLE

                val reviewerTeam = db.teamDao().getById(review.reviewerTeamId)
                val targetTeam = if (review.reviewerTeamId == homeTeam.id) awayTeam else homeTeam

                binding.tvLockedReviewTitle.text = if (review.reviewerTeamId == myTeamId) {
                    "Review kamu untuk ${targetTeam.namaTim}"
                } else {
                    "Review dari ${reviewerTeam?.namaTim ?: "Lawan"} untuk ${targetTeam.namaTim}"
                }

                binding.tvStarsSportivitas.text = renderStars(review.ratingSportivitas)
                binding.tvStarsKetepatan.text = renderStars(review.ratingKetepatanWaktu)
                binding.tvStarsSkillMatch.text = renderStars(review.ratingSkillMatch)
                binding.tvLockedCatatan.text = review.catatanTeks.ifBlank { "-" }
                binding.tvLockedTanggal.text = "Dikirim ${FormatUtil.formatDateFull(review.tanggalSubmit)}"
                binding.tvLockedFooterInfo.text = "Review ini sudah masuk ke rating ${targetTeam.namaTim}."
            } else if (isParticipant && !match.sudahDiReview) {
                binding.cardSudahReview.visibility = View.GONE
                binding.cardBelumReview.visibility = View.VISIBLE
                binding.tvReviewTargetTitle.text = "Review untuk ${opponent.namaTim}"
                binding.btnBukaFormReview.setOnClickListener {
                    val intent = Intent(this@DetailMatchActivity, FormReviewActivity::class.java)
                    intent.putExtra(FormReviewActivity.EXTRA_MATCH_ID, match.id)
                    startActivity(intent)
                }
            } else {
                binding.cardBelumReview.visibility = View.GONE
                binding.cardSudahReview.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_MATCH_ID = "extra_match_id"
    }
}

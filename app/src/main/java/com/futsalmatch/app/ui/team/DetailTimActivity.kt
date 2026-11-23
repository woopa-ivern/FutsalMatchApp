package com.futsalmatch.app.ui.team

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsalmatch.app.R
import com.futsalmatch.app.auth.LoginActivity
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.ActivityDetailTimBinding
import com.futsalmatch.app.model.MatchWithTeams
import com.futsalmatch.app.ui.match.DetailMatchActivity
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class DetailTimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTimBinding
    private lateinit var db: AppDatabase
    private var teamId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        teamId = intent.getIntExtra(EXTRA_TEAM_ID, -1)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvRecentMatches.layoutManager = LinearLayoutManager(this)

        if (teamId == -1) {
            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        loadDetail()
    }

    private fun loadDetail() {
        lifecycleScope.launch {
            val team = db.teamDao().getById(teamId) ?: return@launch
            val players = db.playerDao().getByTeamId(teamId)
            val reviews = db.reviewDao().getReviewsReceivedByTeam(teamId)
            val recentMatches = db.matchDao().getRecent5MatchesForTeam(teamId)

            AvatarHelper.bindTeamAvatar(
                this@DetailTimActivity,
                team.id,
                team.namaTim,
                team.fotoPath,
                binding.ivDetailAvatar,
                binding.tvDetailInitials
            )
            binding.tvDetailNamaTim.text = team.namaTim
            binding.tvDetailKota.text = team.kota
            AvatarHelper.bindSkillBadge(this@DetailTimActivity, team.skillLevel, binding.tvDetailSkillBadge)
            binding.tvDetailDeskripsi.text = team.deskripsiSingkat

            val avg = FormatUtil.computeAverageRating(reviews)
            binding.tvDetailReviewCount.text = "${reviews.size} review"
            binding.tvDetailRatingAvg.text = FormatUtil.formatRating(avg)

            // Anggota chips
            binding.tvAnggotaHeader.text = "Anggota (${players.size})"
            binding.chipGroupAnggota.removeAllViews()
            for (player in players) {
                val chip = Chip(this@DetailTimActivity).apply {
                    text = if (!player.posisi.isNullOrBlank()) {
                        "${player.namaPemain} · ${player.posisi}"
                    } else {
                        player.namaPemain
                    }
                    isClickable = false
                    isCheckable = false
                    setChipBackgroundColorResource(R.color.putih)
                    setChipStrokeColorResource(R.color.garis)
                    chipStrokeWidth = resources.displayMetrics.density
                    setTextColor(getColor(R.color.tinta))
                    textSize = 12f
                }
                binding.chipGroupAnggota.addView(chip)
            }

            // Rating breakdown bars
            if (reviews.isEmpty()) {
                binding.pbSportivitas.progress = 0
                binding.pbKetepatan.progress = 0
                binding.pbSkillMatch.progress = 0
                binding.tvScoreSportivitas.text = "-"
                binding.tvScoreKetepatan.text = "-"
                binding.tvScoreSkillMatch.text = "-"
            } else {
                val avgSport = reviews.map { it.ratingSportivitas }.average()
                val avgTime = reviews.map { it.ratingKetepatanWaktu }.average()
                val avgSkill = reviews.map { it.ratingSkillMatch }.average()

                binding.pbSportivitas.progress = (avgSport * 10).toInt()
                binding.pbKetepatan.progress = (avgTime * 10).toInt()
                binding.pbSkillMatch.progress = (avgSkill * 10).toInt()

                binding.tvScoreSportivitas.text = String.format(Locale.US, "%.1f", avgSport)
                binding.tvScoreKetepatan.text = String.format(Locale.US, "%.1f", avgTime)
                binding.tvScoreSkillMatch.text = String.format(Locale.US, "%.1f", avgSkill)
            }

            // 5 match terakhir
            val matchItems = recentMatches.mapNotNull { match ->
                val home = db.teamDao().getById(match.teamHomeId)
                val away = db.teamDao().getById(match.teamAwayId)
                if (home != null && away != null) MatchWithTeams(match, home, away) else null
            }

            if (matchItems.isEmpty()) {
                binding.rvRecentMatches.visibility = View.GONE
                binding.tvEmptyRecentMatches.visibility = View.VISIBLE
            } else {
                binding.rvRecentMatches.visibility = View.VISIBLE
                binding.tvEmptyRecentMatches.visibility = View.GONE
                binding.rvRecentMatches.adapter = TeamMatchHistoryAdapter(teamId, matchItems) { clicked ->
                    if (SessionManager.isLoggedIn(this@DetailTimActivity)) {
                        val intent = Intent(this@DetailTimActivity, DetailMatchActivity::class.java)
                        intent.putExtra(DetailMatchActivity.EXTRA_MATCH_ID, clicked.match.id)
                        startActivity(intent)
                    }
                }
            }

            // WhatsApp access control (Guest vs Member)
            val isLoggedIn = SessionManager.isLoggedIn(this@DetailTimActivity)
            val myTeamId = SessionManager.getTeamId(this@DetailTimActivity)

            if (!isLoggedIn) {
                binding.layoutWaGuest.visibility = View.VISIBLE
                binding.layoutWaMember.visibility = View.GONE
                binding.btnLoginUntukWa.setOnClickListener {
                    Snackbar.make(
                        binding.root,
                        "Login dulu untuk melihat nomor WA.",
                        Snackbar.LENGTH_LONG
                    ).setAction("Masuk") {
                        startActivity(Intent(this@DetailTimActivity, LoginActivity::class.java))
                    }.show()
                }
            } else if (myTeamId == team.id) {
                binding.layoutWaGuest.visibility = View.GONE
                binding.layoutWaMember.visibility = View.VISIBLE
                binding.tvNomorWa.text = team.noWhatsapp
                binding.btnHubungiWa.text = "Tim kamu"
                binding.btnHubungiWa.isEnabled = false
            } else {
                binding.layoutWaGuest.visibility = View.GONE
                binding.layoutWaMember.visibility = View.VISIBLE
                binding.tvNomorWa.text = team.noWhatsapp
                binding.btnHubungiWa.text = "Hubungi"
                binding.btnHubungiWa.isEnabled = true
                binding.btnHubungiWa.setOnClickListener {
                    openWhatsApp(team.noWhatsapp, team.namaTim)
                }
            }
        }
    }

    private fun openWhatsApp(phoneNumber: String, opponentName: String) {
        val formatted = FormatUtil.formatWhatsappNumberForUrl(phoneNumber)
        val message = Uri.encode("Halo kapten $opponentName, mau ajak sparring futsal lewat FutsalMatch!")
        val url = "https://wa.me/$formatted?text=$message"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Tidak ada aplikasi browser/WhatsApp tersedia.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_TEAM_ID = "extra_team_id"
    }
}

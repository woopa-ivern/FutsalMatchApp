package com.futsalmatch.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsalmatch.app.BaseActivity
import com.futsalmatch.app.R
import com.futsalmatch.app.auth.LoginActivity
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.SkillLevel
import com.futsalmatch.app.databinding.FragmentHomeBinding
import com.futsalmatch.app.ui.match.FormReviewActivity
import com.futsalmatch.app.ui.match.TambahMatchActivity
import com.futsalmatch.app.ui.team.DetailTimActivity
import com.futsalmatch.app.ui.team.TeamAdapter
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import com.futsalmatch.app.util.TeamQueryHelper
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        teamAdapter = TeamAdapter(emptyList()) { summary ->
            val intent = Intent(requireContext(), DetailTimActivity::class.java)
            intent.putExtra(DetailTimActivity.EXTRA_TEAM_ID, summary.team.id)
            startActivity(intent)
        }
        binding.rvHomeTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHomeTeams.adapter = teamAdapter

        setupClicks()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadHomeData()
        }
    }

    private fun setupClicks() {
        val goLogin = View.OnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        binding.btnHeaderMasuk.setOnClickListener(goLogin)
        binding.bannerGuestWaInfo.setOnClickListener(goLogin)

        binding.cardLevelJago.setOnClickListener {
            (activity as? BaseActivity)?.openCariLawanWithSkill(SkillLevel.JAGO)
        }
        binding.cardLevelSedang.setOnClickListener {
            (activity as? BaseActivity)?.openCariLawanWithSkill(SkillLevel.SEDANG)
        }
        binding.cardLevelPemula.setOnClickListener {
            (activity as? BaseActivity)?.openCariLawanWithSkill(SkillLevel.PEMULA)
        }

        binding.btnQuickCatatMatch.setOnClickListener {
            startActivity(Intent(requireContext(), TambahMatchActivity::class.java))
        }
        binding.btnQuickRiwayat.setOnClickListener {
            (activity as? BaseActivity)?.openTab(R.id.nav_riwayat)
        }

        binding.tvLihatSemua.setOnClickListener {
            if (SessionManager.isLoggedIn(requireContext())) {
                (activity as? BaseActivity)?.openTab(R.id.nav_cari)
            } else {
                (activity as? BaseActivity)?.openTab(R.id.nav_tim)
            }
        }
    }

    private fun loadHomeData() {
        val isMember = SessionManager.isLoggedIn(requireContext())
        val myTeamId = SessionManager.getTeamId(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val allSummaries = TeamQueryHelper.getAllTeamSummaries(db)

            if (!isMember) {
                binding.layoutGuestHeader.visibility = View.VISIBLE
                binding.layoutMemberHeader.visibility = View.GONE
                binding.layoutGuestLevels.visibility = View.VISIBLE
                binding.layoutMemberQuickActions.visibility = View.GONE
                binding.layoutPendingReview.visibility = View.GONE
                binding.bannerGuestWaInfo.visibility = View.VISIBLE

                binding.tvHeroTitle.text = "Cari lawan futsa yang seimbang"
                binding.tvHeroSubtitle.visibility = View.VISIBLE
                binding.tvHeroSubtitle.text = "Lihat katalog tanpa login. Nomor WA hanya untuk member."
                binding.btnHeroAction.text = "Masuk atau daftar"
                binding.btnHeroAction.setOnClickListener {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }

                val jagoCount = allSummaries.count { it.team.skillLevel == SkillLevel.JAGO }
                val sedangCount = allSummaries.count { it.team.skillLevel == SkillLevel.SEDANG }
                val pemulaCount = allSummaries.count { it.team.skillLevel == SkillLevel.PEMULA }
                binding.tvCountJago.text = "$jagoCount tim"
                binding.tvCountSedang.text = "$sedangCount tim"
                binding.tvCountPemula.text = "$pemulaCount tim"

                binding.tvSectionListTitle.text = "Rating tertinggi"
                val topRated = allSummaries.sortedByDescending { it.averageRating }.take(3)
                teamAdapter.updateData(topRated)
            } else {
                binding.layoutGuestHeader.visibility = View.GONE
                binding.layoutMemberHeader.visibility = View.VISIBLE
                binding.layoutGuestLevels.visibility = View.GONE
                binding.layoutMemberQuickActions.visibility = View.VISIBLE
                binding.bannerGuestWaInfo.visibility = View.GONE

                val myTeam = db.teamDao().getById(myTeamId)
                if (myTeam != null) {
                    AvatarHelper.bindTeamAvatar(
                        requireContext(),
                        myTeam.id,
                        myTeam.namaTim,
                        myTeam.fotoPath,
                        binding.ivMyTeamAvatar,
                        binding.tvMyTeamInitials
                    )
                    binding.tvMemberTeamName.text = myTeam.namaTim
                    AvatarHelper.bindSkillBadge(requireContext(), myTeam.skillLevel, binding.tvMemberSkillBadge)

                    binding.tvHeroTitle.text = "Cari lawan level${myTeam.skillLevel.label} minggu ini"
                    binding.tvHeroSubtitle.visibility = View.GONE
                    binding.btnHeroAction.text = "Cari lawan"
                    binding.btnHeroAction.setOnClickListener {
                        (activity as? BaseActivity)?.openCariLawanWithSkill(myTeam.skillLevel)
                    }

                    // Pending review section
                    val myMatches = db.matchDao().getMatchesForTeam(myTeam.id)
                    val pendingMatch = myMatches.firstOrNull { !it.sudahDiReview }
                    if (pendingMatch != null) {
                        val homeTeam = db.teamDao().getById(pendingMatch.teamHomeId)
                        val awayTeam = db.teamDao().getById(pendingMatch.teamAwayId)
                        if (homeTeam != null && awayTeam != null) {
                            binding.layoutPendingReview.visibility = View.VISIBLE
                            binding.tvPendingMatchTitle.text =
                                "${homeTeam.namaTim} ${pendingMatch.skorHome} - ${pendingMatch.skorAway} ${awayTeam.namaTim}"
                            binding.tvPendingMatchMeta.text =
                                "${FormatUtil.formatDateWithDay(pendingMatch.tanggal)} · ${pendingMatch.lokasi}"
                            binding.btnPendingIsiReview.setOnClickListener {
                                val intent = Intent(requireContext(), FormReviewActivity::class.java)
                                intent.putExtra(FormReviewActivity.EXTRA_MATCH_ID, pendingMatch.id)
                                startActivity(intent)
                            }
                        } else {
                            binding.layoutPendingReview.visibility = View.GONE
                        }
                    } else {
                        binding.layoutPendingReview.visibility = View.GONE
                    }

                    // Recommended opponents (same level first, then adjacent)
                    binding.tvSectionListTitle.text = "Rekomendasi lawan"
                    val recommended = allSummaries
                        .filter { it.team.id != myTeam.id && myTeam.skillLevel.isSameOrAdjacent(it.team.skillLevel) }
                        .map { summary ->
                            val label = if (summary.team.skillLevel == myTeam.skillLevel) {
                                "Level sama"
                            } else {
                                "Level berdekatan"
                            }
                            summary.copy(matchLabel = label)
                        }
                        .sortedWith(
                            compareBy<com.futsalmatch.app.model.TeamSummary> {
                                if (it.team.skillLevel == myTeam.skillLevel) 0 else 1
                            }.thenByDescending { it.averageRating }
                        )
                        .take(4)

                    teamAdapter.updateData(recommended)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvHomeTeams.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

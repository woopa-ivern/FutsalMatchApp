package com.futsalmatch.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.futsalmatch.app.BaseActivity
import com.futsalmatch.app.R
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.FragmentProfilTimSayaBinding
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil
import com.futsalmatch.app.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfilTimSayaFragment : Fragment() {
    private var _binding: FragmentProfilTimSayaBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilTimSayaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        binding.btnEditTim.setOnClickListener {
            startActivity(Intent(requireContext(), EditTimActivity::class.java))
        }

        binding.btnKeluar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Keluar dari akun?")
                .setMessage("Kamu kembali sebagai tamu.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Keluar") { _, _ ->
                    SessionManager.clearSession(requireContext())
                    val intent = Intent(requireContext(), BaseActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadProfile()
        }
    }

    private fun loadProfile() {
        val myTeamId = SessionManager.getTeamId(requireContext())
        val username = SessionManager.getUsername(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val team = db.teamDao().getById(myTeamId) ?: return@launch
            val players = db.playerDao().getByTeamId(myTeamId)
            val matches = db.matchDao().getMatchesForTeam(myTeamId)
            val reviews = db.reviewDao().getReviewsReceivedByTeam(myTeamId)

            AvatarHelper.bindTeamAvatar(
                requireContext(),
                team.id,
                team.namaTim,
                team.fotoPath,
                binding.ivProfilAvatar,
                binding.tvProfilInitials
            )
            binding.tvProfilNamaTim.text = team.namaTim
            binding.tvProfilKota.text = team.kota
            AvatarHelper.bindSkillBadge(requireContext(), team.skillLevel, binding.tvProfilSkill)

            val winCount = matches.count { FormatUtil.getMatchResultForTeam(it, myTeamId) == "M" }
            val avgRating = FormatUtil.computeAverageRating(reviews)

            binding.tvStatMatch.text = matches.size.toString()
            binding.tvStatMenang.text = winCount.toString()
            binding.tvStatRating.text = FormatUtil.formatRating(avgRating)

            binding.tvProfilAnggotaTitle.text = "Anggota (${players.size})"
            binding.containerProfilAnggota.removeAllViews()
            players.forEachIndexed { index, player ->
                val row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_player_row, binding.containerProfilAnggota, false)
                row.findViewById<TextView>(R.id.tvPlayerInitial).text =
                    player.namaPemain.take(1).uppercase()
                row.findViewById<TextView>(R.id.tvPlayerNameRow).text = player.namaPemain
                row.findViewById<TextView>(R.id.tvPlayerPositionRow).text =
                    player.posisi ?: "Pemain"
                binding.containerProfilAnggota.addView(row)

                if (index < players.lastIndex) {
                    val divider = View(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (resources.displayMetrics.density).toInt().coerceAtLeast(1)
                        )
                        setBackgroundColor(requireContext().getColor(R.color.garis))
                    }
                    binding.containerProfilAnggota.addView(divider)
                }
            }

            binding.tvAccountFooter.text = "Masuk sebagai @$username · Satu akun untuk satu tim"
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

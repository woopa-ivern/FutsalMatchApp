package com.futsalmatch.app.ui.match

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.FragmentRiwayatBinding
import com.futsalmatch.app.model.MatchWithTeams
import com.futsalmatch.app.util.SessionManager
import kotlinx.coroutines.launch

class RiwayatFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: MatchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        val myTeamId = SessionManager.getTeamId(requireContext())
        adapter = MatchHistoryAdapter(myTeamId, emptyList()) { clicked ->
            val intent = Intent(requireContext(), DetailMatchActivity::class.java)
            intent.putExtra(DetailMatchActivity.EXTRA_MATCH_ID, clicked.match.id)
            startActivity(intent)
        }
        binding.rvRiwayatMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatMatch.adapter = adapter

        binding.fabTambahMatch.setOnClickListener {
            startActivity(Intent(requireContext(), TambahMatchActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadMatches()
        }
    }

    private fun loadMatches() {
        val myTeamId = SessionManager.getTeamId(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val matches = db.matchDao().getMatchesForTeam(myTeamId)
            val items = matches.mapNotNull { match ->
                val home = db.teamDao().getById(match.teamHomeId)
                val away = db.teamDao().getById(match.teamAwayId)
                if (home != null && away != null) MatchWithTeams(match, home, away) else null
            }

            binding.tvRiwayatSubtitle.text = "${items.size} match"
            adapter.updateData(items)
            binding.tvEmptyRiwayat.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        binding.rvRiwayatMatch.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

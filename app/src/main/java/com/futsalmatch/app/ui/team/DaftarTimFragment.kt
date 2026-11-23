package com.futsalmatch.app.ui.team

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.databinding.FragmentDaftarTimBinding
import com.futsalmatch.app.model.TeamSummary
import com.futsalmatch.app.util.TeamQueryHelper
import kotlinx.coroutines.launch

class DaftarTimFragment : Fragment() {
    private var _binding: FragmentDaftarTimBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: TeamAdapter
    private var allTeams: List<TeamSummary> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarTimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        adapter = TeamAdapter(emptyList()) { summary ->
            val intent = Intent(requireContext(), DetailTimActivity::class.java)
            intent.putExtra(DetailTimActivity.EXTRA_TEAM_ID, summary.team.id)
            startActivity(intent)
        }
        binding.rvDaftarTim.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaftarTim.adapter = adapter

        binding.etSearchTim.addTextChangedListener {
            applyFilter()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadTeams()
        }
    }

    private fun loadTeams() {
        viewLifecycleOwner.lifecycleScope.launch {
            allTeams = TeamQueryHelper.getAllTeamSummaries(db)
                .sortedByDescending { it.averageRating }
            applyFilter()
        }
    }

    private fun applyFilter() {
        val query = binding.etSearchTim.text.toString().trim().lowercase()
        val filtered = if (query.isEmpty()) {
            allTeams
        } else {
            allTeams.filter {
                it.team.namaTim.lowercase().contains(query) ||
                    it.team.kota.lowercase().contains(query)
            }
        }
        binding.tvTotalTim.text = "${filtered.size} tim"
        adapter.updateData(filtered)
        binding.tvEmptyTim.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        binding.rvDaftarTim.adapter = null
        _binding = null
        super.onDestroyView()
    }
}

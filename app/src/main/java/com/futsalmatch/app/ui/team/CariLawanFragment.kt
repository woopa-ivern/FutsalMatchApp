package com.futsalmatch.app.ui.team

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.futsalmatch.app.R
import com.futsalmatch.app.data.AppDatabase
import com.futsalmatch.app.data.SkillLevel
import com.futsalmatch.app.databinding.FragmentCariLawanBinding
import com.futsalmatch.app.model.TeamSummary
import com.futsalmatch.app.util.SessionManager
import com.futsalmatch.app.util.TeamQueryHelper
import kotlinx.coroutines.launch

class CariLawanFragment : Fragment() {
    private var _binding: FragmentCariLawanBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: TeamAdapter
    private var allTeams: List<TeamSummary> = emptyList()
    private var selectedSkill: SkillLevel? = null
    private var selectedKota: String = "Semua kota"
    private var kotaList: List<String> = listOf("Semua kota")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialSkillName = arguments?.getString(ARG_INITIAL_SKILL)
        if (!initialSkillName.isNullOrEmpty()) {
            selectedSkill = try {
                SkillLevel.valueOf(initialSkillName)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCariLawanBinding.inflate(inflater, container, false)
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
        binding.rvCariLawan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCariLawan.adapter = adapter

        when (selectedSkill) {
            SkillLevel.JAGO -> binding.chipJago.isChecked = true
            SkillLevel.SEDANG -> binding.chipSedang.isChecked = true
            SkillLevel.PEMULA -> binding.chipPemula.isChecked = true
            null -> binding.chipSemua.isChecked = true
        }

        binding.etSearchLawan.addTextChangedListener {
            applyFilter()
        }

        binding.chipGroupLevel.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedSkill = when (checkedIds.firstOrNull()) {
                R.id.chipJago -> SkillLevel.JAGO
                R.id.chipSedang -> SkillLevel.SEDANG
                R.id.chipPemula -> SkillLevel.PEMULA
                else -> null
            }
            applyFilter()
        }

        binding.spinnerKota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in kotaList.indices) {
                    selectedKota = kotaList[position]
                    applyFilter()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadTeams()
        }
    }

    private fun loadTeams() {
        val myTeamId = SessionManager.getTeamId(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val loaded = TeamQueryHelper.getAllTeamSummaries(db)
            allTeams = if (SessionManager.isLoggedIn(requireContext())) {
                loaded.filter { it.team.id != myTeamId }
            } else {
                loaded
            }

            val cities = allTeams.map { it.team.kota }.distinct().sorted()
            kotaList = listOf("Semua kota") + cities
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                kotaList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.spinnerKota.adapter = spinnerAdapter
            val idx = kotaList.indexOf(selectedKota).coerceAtLeast(0)
            binding.spinnerKota.setSelection(idx)

            applyFilter()
        }
    }

    private fun applyFilter() {
        val query = binding.etSearchLawan.text.toString().trim().lowercase()
        val skill = selectedSkill

        if (skill != null) {
            binding.bannerAdjacentInfo.visibility = View.VISIBLE
            binding.tvAdjacentInfo.text = skill.adjacentDescription()
        } else {
            binding.bannerAdjacentInfo.visibility = View.GONE
        }

        val filtered = allTeams
            .filter { summary ->
                val matchesName = query.isEmpty() ||
                    summary.team.namaTim.lowercase().contains(query) ||
                    summary.team.kota.lowercase().contains(query)
                val matchesCity = selectedKota == "Semua kota" ||
                    summary.team.kota.equals(selectedKota, ignoreCase = true)
                val matchesSkill = skill == null || skill.isSameOrAdjacent(summary.team.skillLevel)
                matchesName && matchesCity && matchesSkill
            }
            .map { summary ->
                val badge = if (skill != null) {
                    if (summary.team.skillLevel == skill) "Level sama" else "Level berdekatan"
                } else {
                    null
                }
                summary.copy(matchLabel = badge)
            }
            .sortedWith(
                compareBy<TeamSummary> {
                    if (skill == null || it.team.skillLevel == skill) 0 else 1
                }.thenByDescending { it.averageRating }
            )

        binding.tvMatchCount.text = "${filtered.size} tim cocok"
        adapter.updateData(filtered)
        binding.tvEmptyCari.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        binding.rvCariLawan.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_INITIAL_SKILL = "arg_initial_skill"

        fun newInstance(skill: SkillLevel?): CariLawanFragment {
            return CariLawanFragment().apply {
                arguments = Bundle().apply {
                    if (skill != null) putString(ARG_INITIAL_SKILL, skill.name)
                }
            }
        }
    }
}

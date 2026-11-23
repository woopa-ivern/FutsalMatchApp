package com.futsalmatch.app.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futsalmatch.app.R
import com.futsalmatch.app.databinding.ItemTeamRecentMatchBinding
import com.futsalmatch.app.model.MatchWithTeams
import com.futsalmatch.app.util.FormatUtil

class TeamMatchHistoryAdapter(
    private val targetTeamId: Int,
    private val items: List<MatchWithTeams>,
    private val onItemClick: (MatchWithTeams) -> Unit
) : RecyclerView.Adapter<TeamMatchHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTeamRecentMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeamRecentMatchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val match = item.match
        val isHome = match.teamHomeId == targetTeamId
        val opponent = if (isHome) item.awayTeam else item.homeTeam
        val myScore = if (isHome) match.skorHome else match.skorAway
        val oppScore = if (isHome) match.skorAway else match.skorHome
        val resultCode = FormatUtil.getMatchResultForTeam(match, targetTeamId)

        with(holder.binding) {
            tvResultBadge.text = resultCode
            tvResultBadge.setBackgroundResource(
                when (resultCode) {
                    "M" -> R.drawable.bg_circle_win
                    "K" -> R.drawable.bg_circle_loss
                    else -> R.drawable.bg_circle_draw
                }
            )
            tvOpponentName.text = "vs ${opponent.namaTim}"
            tvScore.text = "$myScore - $oppScore"
            tvDate.text = FormatUtil.formatDateShort(match.tanggal)
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = items.size
}

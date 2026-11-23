package com.futsalmatch.app.ui.team

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futsalmatch.app.R
import com.futsalmatch.app.databinding.ItemTeamCardBinding
import com.futsalmatch.app.model.TeamSummary
import com.futsalmatch.app.util.AvatarHelper
import com.futsalmatch.app.util.FormatUtil

class TeamAdapter(
    private var items: List<TeamSummary>,
    private val onItemClick: (TeamSummary) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(val binding: ItemTeamCardBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newItems: List<TeamSummary>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val summary = items[position]
        val team = summary.team
        val context = holder.itemView.context

        with(holder.binding) {
            AvatarHelper.bindTeamAvatar(
                context = context,
                teamId = team.id,
                teamName = team.namaTim,
                fotoPath = team.fotoPath,
                imageView = ivTeamAvatar,
                initialsView = tvTeamInitials
            )

            tvTeamName.text = team.namaTim
            tvTeamCity.text = team.kota
            tvTeamRating.text = FormatUtil.formatRating(summary.averageRating)
            AvatarHelper.bindSkillBadge(context, team.skillLevel, tvSkillBadge)

            layoutFormCircles.removeAllViews()
            val density = context.resources.displayMetrics.density
            val sizePx = (20 * density).toInt()
            val marginPx = (4 * density).toInt()

            if (summary.recentForm.isEmpty()) {
                val emptyTv = TextView(context).apply {
                    text = "Belum ada match"
                    textSize = 11f
                    setTextColor(context.getColor(R.color.abu_muda))
                }
                layoutFormCircles.addView(emptyTv)
            } else {
                for (result in summary.recentForm) {
                    val badge = TextView(context).apply {
                        text = result
                        textSize = 10f
                        gravity = Gravity.CENTER
                        setTextColor(context.getColor(R.color.putih))
                        setBackgroundResource(
                            when (result) {
                                "M" -> R.drawable.bg_circle_win
                                "K" -> R.drawable.bg_circle_loss
                                else -> R.drawable.bg_circle_draw
                            }
                        )
                        layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                            marginEnd = marginPx
                        }
                    }
                    layoutFormCircles.addView(badge)
                }
            }

            if (!summary.matchLabel.isNullOrEmpty()) {
                tvMatchBadge.visibility = View.VISIBLE
                tvMatchBadge.text = summary.matchLabel
            } else {
                tvMatchBadge.visibility = View.GONE
            }

            root.setOnClickListener { onItemClick(summary) }
        }
    }

    override fun getItemCount(): Int = items.size
}

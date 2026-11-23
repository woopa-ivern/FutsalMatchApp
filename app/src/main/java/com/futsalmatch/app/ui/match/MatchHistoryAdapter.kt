package com.futsalmatch.app.ui.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futsalmatch.app.R
import com.futsalmatch.app.databinding.ItemMatchHistoryBinding
import com.futsalmatch.app.model.MatchWithTeams
import com.futsalmatch.app.util.FormatUtil

class MatchHistoryAdapter(
    private val myTeamId: Int,
    private var items: List<MatchWithTeams>,
    private val onItemClick: (MatchWithTeams) -> Unit
) : RecyclerView.Adapter<MatchHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMatchHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newItems: List<MatchWithTeams>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val match = item.match
        val context = holder.itemView.context

        val resultWord = FormatUtil.getMatchResultWordForTeam(match, myTeamId)
        val resultCode = FormatUtil.getMatchResultForTeam(match, myTeamId)

        with(holder.binding) {
            tvMatchDate.text = FormatUtil.formatDateFull(match.tanggal)
            tvMatchResultWord.text = resultWord
            tvMatchResultWord.setTextColor(
                context.getColor(
                    when (resultCode) {
                        "M" -> R.color.lapangan
                        "K" -> R.color.kartu_orange
                        else -> R.color.abu_teks
                    }
                )
            )
            tvMatchHeadline.text =
                "${item.homeTeam.namaTim} ${match.skorHome} - ${match.skorAway} ${item.awayTeam.namaTim}"
            tvMatchLocation.text = match.lokasi

            if (match.sudahDiReview) {
                tvReviewStatusBadge.text = "Review terkunci"
                tvReviewStatusBadge.setBackgroundResource(R.drawable.bg_badge_terkunci)
                tvReviewStatusBadge.setTextColor(context.getColor(R.color.abu_teks))
            } else {
                tvReviewStatusBadge.text = "Belum direview"
                tvReviewStatusBadge.setBackgroundResource(R.drawable.bg_badge_belum_review)
                tvReviewStatusBadge.setTextColor(context.getColor(R.color.sedang_level))
            }

            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount(): Int = items.size
}

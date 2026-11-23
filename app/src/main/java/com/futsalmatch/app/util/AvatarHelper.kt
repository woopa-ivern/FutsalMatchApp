package com.futsalmatch.app.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.futsalmatch.app.R
import com.futsalmatch.app.data.SkillLevel
import java.io.File

object AvatarHelper {
    private val avatarColors = listOf(
        "#A32A29",
        "#15222E",
        "#25529E",
        "#0B5D4B",
        "#6C3B8E",
        "#C86B1A"
    )

    fun bindTeamAvatar(
        context: Context,
        teamId: Int,
        teamName: String,
        fotoPath: String?,
        imageView: ImageView,
        initialsView: TextView
    ) {
        val file = fotoPath?.let { File(it) }
        if (file != null && file.exists()) {
            initialsView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            Glide.with(context)
                .load(file)
                .circleCrop()
                .into(imageView)
        } else {
            imageView.visibility = View.GONE
            initialsView.visibility = View.VISIBLE
            initialsView.text = FormatUtil.getInitials(teamName)
            val colorHex = avatarColors[kotlin.math.abs(teamId) % avatarColors.size]
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(colorHex))
            }
            initialsView.background = bg
        }
    }

    fun bindSkillBadge(context: Context, skill: SkillLevel, textView: TextView) {
        textView.text = "• ${skill.label}"
        when (skill) {
            SkillLevel.JAGO -> {
                textView.setBackgroundResource(R.drawable.bg_badge_jago)
                textView.setTextColor(context.getColor(R.color.kartu_orange))
            }
            SkillLevel.SEDANG -> {
                textView.setBackgroundResource(R.drawable.bg_badge_sedang)
                textView.setTextColor(context.getColor(R.color.sedang_level))
            }
            SkillLevel.PEMULA -> {
                textView.setBackgroundResource(R.drawable.bg_badge_pemula)
                textView.setTextColor(context.getColor(R.color.langit))
            }
        }
    }
}

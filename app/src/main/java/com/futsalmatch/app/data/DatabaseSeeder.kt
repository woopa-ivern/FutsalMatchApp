package com.futsalmatch.app.data

import com.futsalmatch.app.data.entity.MatchEntity
import com.futsalmatch.app.data.entity.PlayerEntity
import com.futsalmatch.app.data.entity.ReviewEntity
import com.futsalmatch.app.data.entity.TeamEntity
import com.futsalmatch.app.data.entity.UserEntity
import com.futsalmatch.app.util.SecurityUtil
import java.util.Calendar

object DatabaseSeeder {
    private fun dateMillis(year: Int, monthZeroBased: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, monthZeroBased, day, 19, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.teamDao().countAll() > 0) return

        val passHash = SecurityUtil.sha256("123456")

        // 5 Seed Teams matching Blueprint UI
        val garudaId = db.teamDao().insert(
            TeamEntity(
                namaTim = "Garuda FC",
                kota = "Malang",
                skillLevel = SkillLevel.JAGO,
                noWhatsapp = "0812-3456-7890",
                deskripsiSingkat = "Tim alumni teknik, main rutin Sabtu pagi. Cepat, rapi, suka bola pendek.",
                fotoPath = null
            )
        ).toInt()

        val bantengId = db.teamDao().insert(
            TeamEntity(
                namaTim = "Banteng United",
                kota = "Jakarta",
                skillLevel = SkillLevel.JAGO,
                noWhatsapp = "0813-2244-5566",
                deskripsiSingkat = "Tim komunitas kantor Jakarta Selatan. Disiplin jadwal dan suka sparring kompetitif.",
                fotoPath = null
            )
        ).toInt()

        val elangId = db.teamDao().insert(
            TeamEntity(
                namaTim = "Elang Muda",
                kota = "Surabaya",
                skillLevel = SkillLevel.SEDANG,
                noWhatsapp = "0812-5555-0102",
                deskripsiSingkat = "Tim kampus, main rutin Kamis malam. Terbuka untuk uji coba.",
                fotoPath = null
            )
        ).toInt()

        val merpatiId = db.teamDao().insert(
            TeamEntity(
                namaTim = "Merpati FC",
                kota = "Bandung",
                skillLevel = SkillLevel.SEDANG,
                noWhatsapp = "0857-8899-1122",
                deskripsiSingkat = "Tim komunitas Bandung yang rutin main tiap akhir pekan. Mengutamakan sportivitas.",
                fotoPath = null
            )
        ).toInt()

        val rajawaliId = db.teamDao().insert(
            TeamEntity(
                namaTim = "Rajawali FC",
                kota = "Malang",
                skillLevel = SkillLevel.PEMULA,
                noWhatsapp = "0821-7788-3344",
                deskripsiSingkat = "Tim pemula baru terbentuk, fokus fun futsal dan nambah jam terbang bersama.",
                fotoPath = null
            )
        ).toInt()

        // Users (1 account = 1 team)
        db.userDao().insert(UserEntity(username = "kapten_garuda", passwordHash = passHash, teamId = garudaId))
        db.userDao().insert(UserEntity(username = "kapten_banteng", passwordHash = passHash, teamId = bantengId))
        db.userDao().insert(UserEntity(username = "kapten_elang", passwordHash = passHash, teamId = elangId))
        db.userDao().insert(UserEntity(username = "kapten_merpati", passwordHash = passHash, teamId = merpatiId))
        db.userDao().insert(UserEntity(username = "kapten_rajawali", passwordHash = passHash, teamId = rajawaliId))

        // Players (5 per team)
        db.playerDao().insertAll(
            listOf(
                PlayerEntity(teamId = garudaId, namaPemain = "Rizky", posisi = "Kiper"),
                PlayerEntity(teamId = garudaId, namaPemain = "Dimas", posisi = "Anchor"),
                PlayerEntity(teamId = garudaId, namaPemain = "Fajar", posisi = "Flank"),
                PlayerEntity(teamId = garudaId, namaPemain = "Bima", posisi = "Flank"),
                PlayerEntity(teamId = garudaId, namaPemain = "Arga", posisi = "Pivot"),

                PlayerEntity(teamId = bantengId, namaPemain = "Hendra", posisi = "Kiper"),
                PlayerEntity(teamId = bantengId, namaPemain = "Bagas", posisi = "Anchor"),
                PlayerEntity(teamId = bantengId, namaPemain = "Rian", posisi = "Flank"),
                PlayerEntity(teamId = bantengId, namaPemain = "Dika", posisi = "Flank"),
                PlayerEntity(teamId = bantengId, namaPemain = "Doni", posisi = "Pivot"),

                PlayerEntity(teamId = elangId, namaPemain = "Yusuf", posisi = "Kapten · Anchor"),
                PlayerEntity(teamId = elangId, namaPemain = "Naufal", posisi = "Kiper"),
                PlayerEntity(teamId = elangId, namaPemain = "Raka", posisi = "Flank"),
                PlayerEntity(teamId = elangId, namaPemain = "Alif", posisi = "Flank"),
                PlayerEntity(teamId = elangId, namaPemain = "Sidiq", posisi = "Pivot"),

                PlayerEntity(teamId = merpatiId, namaPemain = "Iqbal", posisi = "Kiper"),
                PlayerEntity(teamId = merpatiId, namaPemain = "Reza", posisi = "Anchor"),
                PlayerEntity(teamId = merpatiId, namaPemain = "Fahmi", posisi = "Flank"),
                PlayerEntity(teamId = merpatiId, namaPemain = "Yoga", posisi = "Flank"),
                PlayerEntity(teamId = merpatiId, namaPemain = "Fikri", posisi = "Pivot"),

                PlayerEntity(teamId = rajawaliId, namaPemain = "Ilham", posisi = "Kiper"),
                PlayerEntity(teamId = rajawaliId, namaPemain = "Dani", posisi = "Anchor"),
                PlayerEntity(teamId = rajawaliId, namaPemain = "Kevin", posisi = "Flank"),
                PlayerEntity(teamId = rajawaliId, namaPemain = "Aldo", posisi = "Flank"),
                PlayerEntity(teamId = rajawaliId, namaPemain = "Wahyu", posisi = "Pivot")
            )
        )

        // Matches matching Blueprint 5 & 6
        val m1 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = elangId,
                teamAwayId = merpatiId,
                skorHome = 3,
                skorAway = 2,
                tanggal = dateMillis(2026, Calendar.OCTOBER, 8),
                lokasi = "Lapangan Pogot, Surabaya",
                sudahDiReview = false
            )
        ).toInt()

        val m2 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = garudaId,
                teamAwayId = bantengId,
                skorHome = 3,
                skorAway = 3,
                tanggal = dateMillis(2026, Calendar.OCTOBER, 4),
                lokasi = "GOR Ken Arok, Malang",
                sudahDiReview = true
            )
        ).toInt()

        val m3 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = elangId,
                teamAwayId = garudaId,
                skorHome = 2,
                skorAway = 4,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 27),
                lokasi = "GOR Tlogomas, Malang",
                sudahDiReview = true
            )
        ).toInt()

        val m4 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = garudaId,
                teamAwayId = merpatiId,
                skorHome = 5,
                skorAway = 1,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 20),
                lokasi = "Sudimoro Futsal, Malang",
                sudahDiReview = true
            )
        ).toInt()

        val m5 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = elangId,
                teamAwayId = rajawaliId,
                skorHome = 7,
                skorAway = 1,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 15),
                lokasi = "Lapangan Pogot, Surabaya",
                sudahDiReview = true
            )
        ).toInt()

        val m6 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = garudaId,
                teamAwayId = rajawaliId,
                skorHome = 6,
                skorAway = 0,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 13),
                lokasi = "GOR Tlogomas, Malang",
                sudahDiReview = true
            )
        ).toInt()

        val m7 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = bantengId,
                teamAwayId = garudaId,
                skorHome = 3,
                skorAway = 2,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 6),
                lokasi = "Tifosi Sport Center, Jakarta",
                sudahDiReview = true
            )
        ).toInt()

        val m8 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = elangId,
                teamAwayId = bantengId,
                skorHome = 1,
                skorAway = 5,
                tanggal = dateMillis(2026, Calendar.SEPTEMBER, 2),
                lokasi = "Arena Wiyung, Surabaya",
                sudahDiReview = false
            )
        ).toInt()

        val m9 = db.matchDao().insert(
            MatchEntity(
                teamHomeId = elangId,
                teamAwayId = merpatiId,
                skorHome = 2,
                skorAway = 2,
                tanggal = dateMillis(2026, Calendar.AUGUST, 24),
                lokasi = "Lapangan Pogot, Surabaya",
                sudahDiReview = true
            )
        ).toInt()

        // Locked Reviews matching Blueprint 6
        db.reviewDao().insert(
            ReviewEntity(
                matchId = m2,
                reviewerTeamId = bantengId,
                ratingSportivitas = 5,
                ratingKetepatanWaktu = 5,
                ratingSkillMatch = 5,
                catatanTeks = "Pertandingan sangat imbang dan sportif.",
                tanggalSubmit = dateMillis(2026, Calendar.OCTOBER, 5),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m3,
                reviewerTeamId = elangId,
                ratingSportivitas = 5,
                ratingKetepatanWaktu = 5,
                ratingSkillMatch = 4,
                catatanTeks = "Permainan cepat dan rapi. Datang 10 menit lebih awal dan ramah.",
                tanggalSubmit = dateMillis(2026, Calendar.SEPTEMBER, 28),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m4,
                reviewerTeamId = merpatiId,
                ratingSportivitas = 5,
                ratingKetepatanWaktu = 4,
                ratingSkillMatch = 5,
                catatanTeks = "Tim kuat dan fair play sepanjang laga.",
                tanggalSubmit = dateMillis(2026, Calendar.SEPTEMBER, 21),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m5,
                reviewerTeamId = rajawaliId,
                ratingSportivitas = 5,
                ratingKetepatanWaktu = 4,
                ratingSkillMatch = 4,
                catatanTeks = "Elang Muda main rapi dan suportif dengan tim pemula.",
                tanggalSubmit = dateMillis(2026, Calendar.SEPTEMBER, 16),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m6,
                reviewerTeamId = garudaId,
                ratingSportivitas = 4,
                ratingKetepatanWaktu = 4,
                ratingSkillMatch = 4,
                catatanTeks = "Semangat bagus dan tepat waktu.",
                tanggalSubmit = dateMillis(2026, Calendar.SEPTEMBER, 14),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m7,
                reviewerTeamId = garudaId,
                ratingSportivitas = 5,
                ratingKetepatanWaktu = 4,
                ratingSkillMatch = 5,
                catatanTeks = "Banteng United solid dan sangat kompetitif.",
                tanggalSubmit = dateMillis(2026, Calendar.SEPTEMBER, 7),
                isLocked = true
            )
        )

        db.reviewDao().insert(
            ReviewEntity(
                matchId = m9,
                reviewerTeamId = elangId,
                ratingSportivitas = 4,
                ratingKetepatanWaktu = 4,
                ratingSkillMatch = 5,
                catatanTeks = "Laga seru dan seimbang sampai peluit akhir.",
                tanggalSubmit = dateMillis(2026, Calendar.AUGUST, 25),
                isLocked = true
            )
        )
    }
}

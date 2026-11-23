package com.futsalmatch.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.futsalmatch.app.data.entity.ReviewEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReviewDao_Impl implements ReviewDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReviewEntity> __insertionAdapterOfReviewEntity;

  public ReviewDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReviewEntity = new EntityInsertionAdapter<ReviewEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reviews` (`id`,`matchId`,`reviewerTeamId`,`ratingSportivitas`,`ratingKetepatanWaktu`,`ratingSkillMatch`,`catatanTeks`,`tanggalSubmit`,`isLocked`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReviewEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMatchId());
        statement.bindLong(3, entity.getReviewerTeamId());
        statement.bindLong(4, entity.getRatingSportivitas());
        statement.bindLong(5, entity.getRatingKetepatanWaktu());
        statement.bindLong(6, entity.getRatingSkillMatch());
        statement.bindString(7, entity.getCatatanTeks());
        statement.bindLong(8, entity.getTanggalSubmit());
        final int _tmp = entity.isLocked() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
  }

  @Override
  public Object insert(final ReviewEntity review, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReviewEntity.insertAndReturnId(review);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByMatchId(final int matchId,
      final Continuation<? super ReviewEntity> $completion) {
    final String _sql = "SELECT * FROM reviews WHERE matchId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReviewEntity>() {
      @Override
      @Nullable
      public ReviewEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfReviewerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewerTeamId");
          final int _cursorIndexOfRatingSportivitas = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingSportivitas");
          final int _cursorIndexOfRatingKetepatanWaktu = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingKetepatanWaktu");
          final int _cursorIndexOfRatingSkillMatch = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingSkillMatch");
          final int _cursorIndexOfCatatanTeks = CursorUtil.getColumnIndexOrThrow(_cursor, "catatanTeks");
          final int _cursorIndexOfTanggalSubmit = CursorUtil.getColumnIndexOrThrow(_cursor, "tanggalSubmit");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLocked");
          final ReviewEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpMatchId;
            _tmpMatchId = _cursor.getInt(_cursorIndexOfMatchId);
            final int _tmpReviewerTeamId;
            _tmpReviewerTeamId = _cursor.getInt(_cursorIndexOfReviewerTeamId);
            final int _tmpRatingSportivitas;
            _tmpRatingSportivitas = _cursor.getInt(_cursorIndexOfRatingSportivitas);
            final int _tmpRatingKetepatanWaktu;
            _tmpRatingKetepatanWaktu = _cursor.getInt(_cursorIndexOfRatingKetepatanWaktu);
            final int _tmpRatingSkillMatch;
            _tmpRatingSkillMatch = _cursor.getInt(_cursorIndexOfRatingSkillMatch);
            final String _tmpCatatanTeks;
            _tmpCatatanTeks = _cursor.getString(_cursorIndexOfCatatanTeks);
            final long _tmpTanggalSubmit;
            _tmpTanggalSubmit = _cursor.getLong(_cursorIndexOfTanggalSubmit);
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            _result = new ReviewEntity(_tmpId,_tmpMatchId,_tmpReviewerTeamId,_tmpRatingSportivitas,_tmpRatingKetepatanWaktu,_tmpRatingSkillMatch,_tmpCatatanTeks,_tmpTanggalSubmit,_tmpIsLocked);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getReviewsReceivedByTeam(final int teamId,
      final Continuation<? super List<ReviewEntity>> $completion) {
    final String _sql = "SELECT r.* FROM reviews r INNER JOIN matches m ON r.matchId = m.id WHERE (m.teamHomeId = ? OR m.teamAwayId = ?) AND r.reviewerTeamId != ? ORDER BY r.tanggalSubmit DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, teamId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, teamId);
    _argIndex = 3;
    _statement.bindLong(_argIndex, teamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReviewEntity>>() {
      @Override
      @NonNull
      public List<ReviewEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfReviewerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewerTeamId");
          final int _cursorIndexOfRatingSportivitas = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingSportivitas");
          final int _cursorIndexOfRatingKetepatanWaktu = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingKetepatanWaktu");
          final int _cursorIndexOfRatingSkillMatch = CursorUtil.getColumnIndexOrThrow(_cursor, "ratingSkillMatch");
          final int _cursorIndexOfCatatanTeks = CursorUtil.getColumnIndexOrThrow(_cursor, "catatanTeks");
          final int _cursorIndexOfTanggalSubmit = CursorUtil.getColumnIndexOrThrow(_cursor, "tanggalSubmit");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLocked");
          final List<ReviewEntity> _result = new ArrayList<ReviewEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReviewEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpMatchId;
            _tmpMatchId = _cursor.getInt(_cursorIndexOfMatchId);
            final int _tmpReviewerTeamId;
            _tmpReviewerTeamId = _cursor.getInt(_cursorIndexOfReviewerTeamId);
            final int _tmpRatingSportivitas;
            _tmpRatingSportivitas = _cursor.getInt(_cursorIndexOfRatingSportivitas);
            final int _tmpRatingKetepatanWaktu;
            _tmpRatingKetepatanWaktu = _cursor.getInt(_cursorIndexOfRatingKetepatanWaktu);
            final int _tmpRatingSkillMatch;
            _tmpRatingSkillMatch = _cursor.getInt(_cursorIndexOfRatingSkillMatch);
            final String _tmpCatatanTeks;
            _tmpCatatanTeks = _cursor.getString(_cursorIndexOfCatatanTeks);
            final long _tmpTanggalSubmit;
            _tmpTanggalSubmit = _cursor.getLong(_cursorIndexOfTanggalSubmit);
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            _item = new ReviewEntity(_tmpId,_tmpMatchId,_tmpReviewerTeamId,_tmpRatingSportivitas,_tmpRatingKetepatanWaktu,_tmpRatingSkillMatch,_tmpCatatanTeks,_tmpTanggalSubmit,_tmpIsLocked);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

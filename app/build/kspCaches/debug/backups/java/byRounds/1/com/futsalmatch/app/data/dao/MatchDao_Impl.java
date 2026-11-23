package com.futsalmatch.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.futsalmatch.app.data.entity.MatchEntity;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MatchDao_Impl implements MatchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchEntity> __insertionAdapterOfMatchEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkReviewed;

  public MatchDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchEntity = new EntityInsertionAdapter<MatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `matches` (`id`,`teamHomeId`,`teamAwayId`,`skorHome`,`skorAway`,`tanggal`,`lokasi`,`sudahDiReview`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTeamHomeId());
        statement.bindLong(3, entity.getTeamAwayId());
        statement.bindLong(4, entity.getSkorHome());
        statement.bindLong(5, entity.getSkorAway());
        statement.bindLong(6, entity.getTanggal());
        statement.bindString(7, entity.getLokasi());
        final int _tmp = entity.getSudahDiReview() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__preparedStmtOfMarkReviewed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE matches SET sudahDiReview = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MatchEntity match, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMatchEntity.insertAndReturnId(match);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markReviewed(final int matchId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkReviewed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, matchId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkReviewed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getMatchesForTeam(final int teamId,
      final Continuation<? super List<MatchEntity>> $completion) {
    final String _sql = "SELECT * FROM matches WHERE teamHomeId = ? OR teamAwayId = ? ORDER BY tanggal DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, teamId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, teamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTeamHomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamHomeId");
          final int _cursorIndexOfTeamAwayId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAwayId");
          final int _cursorIndexOfSkorHome = CursorUtil.getColumnIndexOrThrow(_cursor, "skorHome");
          final int _cursorIndexOfSkorAway = CursorUtil.getColumnIndexOrThrow(_cursor, "skorAway");
          final int _cursorIndexOfTanggal = CursorUtil.getColumnIndexOrThrow(_cursor, "tanggal");
          final int _cursorIndexOfLokasi = CursorUtil.getColumnIndexOrThrow(_cursor, "lokasi");
          final int _cursorIndexOfSudahDiReview = CursorUtil.getColumnIndexOrThrow(_cursor, "sudahDiReview");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpTeamHomeId;
            _tmpTeamHomeId = _cursor.getInt(_cursorIndexOfTeamHomeId);
            final int _tmpTeamAwayId;
            _tmpTeamAwayId = _cursor.getInt(_cursorIndexOfTeamAwayId);
            final int _tmpSkorHome;
            _tmpSkorHome = _cursor.getInt(_cursorIndexOfSkorHome);
            final int _tmpSkorAway;
            _tmpSkorAway = _cursor.getInt(_cursorIndexOfSkorAway);
            final long _tmpTanggal;
            _tmpTanggal = _cursor.getLong(_cursorIndexOfTanggal);
            final String _tmpLokasi;
            _tmpLokasi = _cursor.getString(_cursorIndexOfLokasi);
            final boolean _tmpSudahDiReview;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSudahDiReview);
            _tmpSudahDiReview = _tmp != 0;
            _item = new MatchEntity(_tmpId,_tmpTeamHomeId,_tmpTeamAwayId,_tmpSkorHome,_tmpSkorAway,_tmpTanggal,_tmpLokasi,_tmpSudahDiReview);
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

  @Override
  public Object getRecent5MatchesForTeam(final int teamId,
      final Continuation<? super List<MatchEntity>> $completion) {
    final String _sql = "SELECT * FROM matches WHERE teamHomeId = ? OR teamAwayId = ? ORDER BY tanggal DESC, id DESC LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, teamId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, teamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTeamHomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamHomeId");
          final int _cursorIndexOfTeamAwayId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAwayId");
          final int _cursorIndexOfSkorHome = CursorUtil.getColumnIndexOrThrow(_cursor, "skorHome");
          final int _cursorIndexOfSkorAway = CursorUtil.getColumnIndexOrThrow(_cursor, "skorAway");
          final int _cursorIndexOfTanggal = CursorUtil.getColumnIndexOrThrow(_cursor, "tanggal");
          final int _cursorIndexOfLokasi = CursorUtil.getColumnIndexOrThrow(_cursor, "lokasi");
          final int _cursorIndexOfSudahDiReview = CursorUtil.getColumnIndexOrThrow(_cursor, "sudahDiReview");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpTeamHomeId;
            _tmpTeamHomeId = _cursor.getInt(_cursorIndexOfTeamHomeId);
            final int _tmpTeamAwayId;
            _tmpTeamAwayId = _cursor.getInt(_cursorIndexOfTeamAwayId);
            final int _tmpSkorHome;
            _tmpSkorHome = _cursor.getInt(_cursorIndexOfSkorHome);
            final int _tmpSkorAway;
            _tmpSkorAway = _cursor.getInt(_cursorIndexOfSkorAway);
            final long _tmpTanggal;
            _tmpTanggal = _cursor.getLong(_cursorIndexOfTanggal);
            final String _tmpLokasi;
            _tmpLokasi = _cursor.getString(_cursorIndexOfLokasi);
            final boolean _tmpSudahDiReview;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSudahDiReview);
            _tmpSudahDiReview = _tmp != 0;
            _item = new MatchEntity(_tmpId,_tmpTeamHomeId,_tmpTeamAwayId,_tmpSkorHome,_tmpSkorAway,_tmpTanggal,_tmpLokasi,_tmpSudahDiReview);
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

  @Override
  public Object getById(final int matchId, final Continuation<? super MatchEntity> $completion) {
    final String _sql = "SELECT * FROM matches WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MatchEntity>() {
      @Override
      @Nullable
      public MatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTeamHomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamHomeId");
          final int _cursorIndexOfTeamAwayId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAwayId");
          final int _cursorIndexOfSkorHome = CursorUtil.getColumnIndexOrThrow(_cursor, "skorHome");
          final int _cursorIndexOfSkorAway = CursorUtil.getColumnIndexOrThrow(_cursor, "skorAway");
          final int _cursorIndexOfTanggal = CursorUtil.getColumnIndexOrThrow(_cursor, "tanggal");
          final int _cursorIndexOfLokasi = CursorUtil.getColumnIndexOrThrow(_cursor, "lokasi");
          final int _cursorIndexOfSudahDiReview = CursorUtil.getColumnIndexOrThrow(_cursor, "sudahDiReview");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpTeamHomeId;
            _tmpTeamHomeId = _cursor.getInt(_cursorIndexOfTeamHomeId);
            final int _tmpTeamAwayId;
            _tmpTeamAwayId = _cursor.getInt(_cursorIndexOfTeamAwayId);
            final int _tmpSkorHome;
            _tmpSkorHome = _cursor.getInt(_cursorIndexOfSkorHome);
            final int _tmpSkorAway;
            _tmpSkorAway = _cursor.getInt(_cursorIndexOfSkorAway);
            final long _tmpTanggal;
            _tmpTanggal = _cursor.getLong(_cursorIndexOfTanggal);
            final String _tmpLokasi;
            _tmpLokasi = _cursor.getString(_cursorIndexOfLokasi);
            final boolean _tmpSudahDiReview;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSudahDiReview);
            _tmpSudahDiReview = _tmp != 0;
            _result = new MatchEntity(_tmpId,_tmpTeamHomeId,_tmpTeamAwayId,_tmpSkorHome,_tmpSkorAway,_tmpTanggal,_tmpLokasi,_tmpSudahDiReview);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

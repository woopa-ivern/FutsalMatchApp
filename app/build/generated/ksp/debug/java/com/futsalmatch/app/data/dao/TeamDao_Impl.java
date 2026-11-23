package com.futsalmatch.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.futsalmatch.app.data.Converters;
import com.futsalmatch.app.data.SkillLevel;
import com.futsalmatch.app.data.entity.TeamEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class TeamDao_Impl implements TeamDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TeamEntity> __insertionAdapterOfTeamEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<TeamEntity> __updateAdapterOfTeamEntity;

  public TeamDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTeamEntity = new EntityInsertionAdapter<TeamEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `teams` (`id`,`namaTim`,`kota`,`skillLevel`,`noWhatsapp`,`deskripsiSingkat`,`fotoPath`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TeamEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNamaTim());
        statement.bindString(3, entity.getKota());
        final String _tmp = __converters.fromSkillLevel(entity.getSkillLevel());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getNoWhatsapp());
        statement.bindString(6, entity.getDeskripsiSingkat());
        if (entity.getFotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFotoPath());
        }
      }
    };
    this.__updateAdapterOfTeamEntity = new EntityDeletionOrUpdateAdapter<TeamEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `teams` SET `id` = ?,`namaTim` = ?,`kota` = ?,`skillLevel` = ?,`noWhatsapp` = ?,`deskripsiSingkat` = ?,`fotoPath` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TeamEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNamaTim());
        statement.bindString(3, entity.getKota());
        final String _tmp = __converters.fromSkillLevel(entity.getSkillLevel());
        statement.bindString(4, _tmp);
        statement.bindString(5, entity.getNoWhatsapp());
        statement.bindString(6, entity.getDeskripsiSingkat());
        if (entity.getFotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getFotoPath());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final TeamEntity team, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTeamEntity.insertAndReturnId(team);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TeamEntity team, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTeamEntity.handle(team);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<TeamEntity>> $completion) {
    final String _sql = "SELECT * FROM teams ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TeamEntity>>() {
      @Override
      @NonNull
      public List<TeamEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNamaTim = CursorUtil.getColumnIndexOrThrow(_cursor, "namaTim");
          final int _cursorIndexOfKota = CursorUtil.getColumnIndexOrThrow(_cursor, "kota");
          final int _cursorIndexOfSkillLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "skillLevel");
          final int _cursorIndexOfNoWhatsapp = CursorUtil.getColumnIndexOrThrow(_cursor, "noWhatsapp");
          final int _cursorIndexOfDeskripsiSingkat = CursorUtil.getColumnIndexOrThrow(_cursor, "deskripsiSingkat");
          final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
          final List<TeamEntity> _result = new ArrayList<TeamEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TeamEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNamaTim;
            _tmpNamaTim = _cursor.getString(_cursorIndexOfNamaTim);
            final String _tmpKota;
            _tmpKota = _cursor.getString(_cursorIndexOfKota);
            final SkillLevel _tmpSkillLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSkillLevel);
            _tmpSkillLevel = __converters.toSkillLevel(_tmp);
            final String _tmpNoWhatsapp;
            _tmpNoWhatsapp = _cursor.getString(_cursorIndexOfNoWhatsapp);
            final String _tmpDeskripsiSingkat;
            _tmpDeskripsiSingkat = _cursor.getString(_cursorIndexOfDeskripsiSingkat);
            final String _tmpFotoPath;
            if (_cursor.isNull(_cursorIndexOfFotoPath)) {
              _tmpFotoPath = null;
            } else {
              _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
            }
            _item = new TeamEntity(_tmpId,_tmpNamaTim,_tmpKota,_tmpSkillLevel,_tmpNoWhatsapp,_tmpDeskripsiSingkat,_tmpFotoPath);
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
  public Object getById(final int teamId, final Continuation<? super TeamEntity> $completion) {
    final String _sql = "SELECT * FROM teams WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, teamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TeamEntity>() {
      @Override
      @Nullable
      public TeamEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNamaTim = CursorUtil.getColumnIndexOrThrow(_cursor, "namaTim");
          final int _cursorIndexOfKota = CursorUtil.getColumnIndexOrThrow(_cursor, "kota");
          final int _cursorIndexOfSkillLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "skillLevel");
          final int _cursorIndexOfNoWhatsapp = CursorUtil.getColumnIndexOrThrow(_cursor, "noWhatsapp");
          final int _cursorIndexOfDeskripsiSingkat = CursorUtil.getColumnIndexOrThrow(_cursor, "deskripsiSingkat");
          final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
          final TeamEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNamaTim;
            _tmpNamaTim = _cursor.getString(_cursorIndexOfNamaTim);
            final String _tmpKota;
            _tmpKota = _cursor.getString(_cursorIndexOfKota);
            final SkillLevel _tmpSkillLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSkillLevel);
            _tmpSkillLevel = __converters.toSkillLevel(_tmp);
            final String _tmpNoWhatsapp;
            _tmpNoWhatsapp = _cursor.getString(_cursorIndexOfNoWhatsapp);
            final String _tmpDeskripsiSingkat;
            _tmpDeskripsiSingkat = _cursor.getString(_cursorIndexOfDeskripsiSingkat);
            final String _tmpFotoPath;
            if (_cursor.isNull(_cursorIndexOfFotoPath)) {
              _tmpFotoPath = null;
            } else {
              _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
            }
            _result = new TeamEntity(_tmpId,_tmpNamaTim,_tmpKota,_tmpSkillLevel,_tmpNoWhatsapp,_tmpDeskripsiSingkat,_tmpFotoPath);
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
  public Object findByNama(final String namaTim,
      final Continuation<? super TeamEntity> $completion) {
    final String _sql = "SELECT * FROM teams WHERE LOWER(namaTim) = LOWER(?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, namaTim);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TeamEntity>() {
      @Override
      @Nullable
      public TeamEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNamaTim = CursorUtil.getColumnIndexOrThrow(_cursor, "namaTim");
          final int _cursorIndexOfKota = CursorUtil.getColumnIndexOrThrow(_cursor, "kota");
          final int _cursorIndexOfSkillLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "skillLevel");
          final int _cursorIndexOfNoWhatsapp = CursorUtil.getColumnIndexOrThrow(_cursor, "noWhatsapp");
          final int _cursorIndexOfDeskripsiSingkat = CursorUtil.getColumnIndexOrThrow(_cursor, "deskripsiSingkat");
          final int _cursorIndexOfFotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "fotoPath");
          final TeamEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNamaTim;
            _tmpNamaTim = _cursor.getString(_cursorIndexOfNamaTim);
            final String _tmpKota;
            _tmpKota = _cursor.getString(_cursorIndexOfKota);
            final SkillLevel _tmpSkillLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSkillLevel);
            _tmpSkillLevel = __converters.toSkillLevel(_tmp);
            final String _tmpNoWhatsapp;
            _tmpNoWhatsapp = _cursor.getString(_cursorIndexOfNoWhatsapp);
            final String _tmpDeskripsiSingkat;
            _tmpDeskripsiSingkat = _cursor.getString(_cursorIndexOfDeskripsiSingkat);
            final String _tmpFotoPath;
            if (_cursor.isNull(_cursorIndexOfFotoPath)) {
              _tmpFotoPath = null;
            } else {
              _tmpFotoPath = _cursor.getString(_cursorIndexOfFotoPath);
            }
            _result = new TeamEntity(_tmpId,_tmpNamaTim,_tmpKota,_tmpSkillLevel,_tmpNoWhatsapp,_tmpDeskripsiSingkat,_tmpFotoPath);
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
  public Object countAll(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM teams";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

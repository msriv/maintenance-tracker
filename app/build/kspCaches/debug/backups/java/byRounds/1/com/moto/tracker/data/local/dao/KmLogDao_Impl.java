package com.moto.tracker.data.local.dao;

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
import com.moto.tracker.data.local.entity.KmLogEntity;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class KmLogDao_Impl implements KmLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<KmLogEntity> __insertionAdapterOfKmLogEntity;

  private final EntityDeletionOrUpdateAdapter<KmLogEntity> __deletionAdapterOfKmLogEntity;

  public KmLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfKmLogEntity = new EntityInsertionAdapter<KmLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `km_logs` (`id`,`vehicleId`,`logDate`,`odometer`,`kmDriven`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final KmLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getLogDate());
        statement.bindLong(4, entity.getOdometer());
        statement.bindLong(5, entity.getKmDriven());
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNotes());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfKmLogEntity = new EntityDeletionOrUpdateAdapter<KmLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `km_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final KmLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final KmLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfKmLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final KmLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfKmLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<KmLogEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM km_logs WHERE vehicleId = ? ORDER BY logDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"km_logs"}, new Callable<List<KmLogEntity>>() {
      @Override
      @NonNull
      public List<KmLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfLogDate = CursorUtil.getColumnIndexOrThrow(_cursor, "logDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfKmDriven = CursorUtil.getColumnIndexOrThrow(_cursor, "kmDriven");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<KmLogEntity> _result = new ArrayList<KmLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final KmLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpLogDate;
            _tmpLogDate = _cursor.getLong(_cursorIndexOfLogDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final int _tmpKmDriven;
            _tmpKmDriven = _cursor.getInt(_cursorIndexOfKmDriven);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new KmLogEntity(_tmpId,_tmpVehicleId,_tmpLogDate,_tmpOdometer,_tmpKmDriven,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByVehicleAndDate(final long vehicleId, final long date,
      final Continuation<? super KmLogEntity> $completion) {
    final String _sql = "SELECT * FROM km_logs WHERE vehicleId = ? AND logDate = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<KmLogEntity>() {
      @Override
      @Nullable
      public KmLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfLogDate = CursorUtil.getColumnIndexOrThrow(_cursor, "logDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfKmDriven = CursorUtil.getColumnIndexOrThrow(_cursor, "kmDriven");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final KmLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpLogDate;
            _tmpLogDate = _cursor.getLong(_cursorIndexOfLogDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final int _tmpKmDriven;
            _tmpKmDriven = _cursor.getInt(_cursorIndexOfKmDriven);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new KmLogEntity(_tmpId,_tmpVehicleId,_tmpLogDate,_tmpOdometer,_tmpKmDriven,_tmpNotes,_tmpCreatedAt);
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
  public Object getLatestByVehicle(final long vehicleId,
      final Continuation<? super KmLogEntity> $completion) {
    final String _sql = "SELECT * FROM km_logs WHERE vehicleId = ? ORDER BY logDate DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<KmLogEntity>() {
      @Override
      @Nullable
      public KmLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfLogDate = CursorUtil.getColumnIndexOrThrow(_cursor, "logDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfKmDriven = CursorUtil.getColumnIndexOrThrow(_cursor, "kmDriven");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final KmLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpLogDate;
            _tmpLogDate = _cursor.getLong(_cursorIndexOfLogDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final int _tmpKmDriven;
            _tmpKmDriven = _cursor.getInt(_cursorIndexOfKmDriven);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new KmLogEntity(_tmpId,_tmpVehicleId,_tmpLogDate,_tmpOdometer,_tmpKmDriven,_tmpNotes,_tmpCreatedAt);
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
  public Object getTotalKmInRange(final long vehicleId, final long from, final long to,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(kmDriven) FROM km_logs WHERE vehicleId = ? AND logDate BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Object getRecentByVehicle(final long vehicleId, final int limit,
      final Continuation<? super List<KmLogEntity>> $completion) {
    final String _sql = "SELECT * FROM km_logs WHERE vehicleId = ? ORDER BY logDate DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<KmLogEntity>>() {
      @Override
      @NonNull
      public List<KmLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfLogDate = CursorUtil.getColumnIndexOrThrow(_cursor, "logDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfKmDriven = CursorUtil.getColumnIndexOrThrow(_cursor, "kmDriven");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<KmLogEntity> _result = new ArrayList<KmLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final KmLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpLogDate;
            _tmpLogDate = _cursor.getLong(_cursorIndexOfLogDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final int _tmpKmDriven;
            _tmpKmDriven = _cursor.getInt(_cursorIndexOfKmDriven);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new KmLogEntity(_tmpId,_tmpVehicleId,_tmpLogDate,_tmpOdometer,_tmpKmDriven,_tmpNotes,_tmpCreatedAt);
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

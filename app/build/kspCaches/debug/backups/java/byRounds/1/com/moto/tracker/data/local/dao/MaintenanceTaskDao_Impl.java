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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.moto.tracker.data.local.entity.MaintenanceTaskEntity;
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
public final class MaintenanceTaskDao_Impl implements MaintenanceTaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MaintenanceTaskEntity> __insertionAdapterOfMaintenanceTaskEntity;

  private final EntityDeletionOrUpdateAdapter<MaintenanceTaskEntity> __updateAdapterOfMaintenanceTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAfterLog;

  private final SharedSQLiteStatement __preparedStmtOfDeactivate;

  public MaintenanceTaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMaintenanceTaskEntity = new EntityInsertionAdapter<MaintenanceTaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `maintenance_tasks` (`id`,`vehicleId`,`taskType`,`customName`,`thresholdDays`,`thresholdKm`,`lastPerformedDate`,`lastPerformedOdometer`,`nextDueDate`,`nextDueOdometer`,`isActive`,`notes`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaintenanceTaskEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getTaskType());
        if (entity.getCustomName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomName());
        }
        if (entity.getThresholdDays() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getThresholdDays());
        }
        if (entity.getThresholdKm() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getThresholdKm());
        }
        if (entity.getLastPerformedDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastPerformedDate());
        }
        if (entity.getLastPerformedOdometer() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastPerformedOdometer());
        }
        if (entity.getNextDueDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextDueDate());
        }
        if (entity.getNextDueOdometer() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNextDueOdometer());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(11, _tmp);
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindLong(14, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfMaintenanceTaskEntity = new EntityDeletionOrUpdateAdapter<MaintenanceTaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `maintenance_tasks` SET `id` = ?,`vehicleId` = ?,`taskType` = ?,`customName` = ?,`thresholdDays` = ?,`thresholdKm` = ?,`lastPerformedDate` = ?,`lastPerformedOdometer` = ?,`nextDueDate` = ?,`nextDueOdometer` = ?,`isActive` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaintenanceTaskEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getTaskType());
        if (entity.getCustomName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCustomName());
        }
        if (entity.getThresholdDays() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getThresholdDays());
        }
        if (entity.getThresholdKm() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getThresholdKm());
        }
        if (entity.getLastPerformedDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastPerformedDate());
        }
        if (entity.getLastPerformedOdometer() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastPerformedOdometer());
        }
        if (entity.getNextDueDate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextDueDate());
        }
        if (entity.getNextDueOdometer() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNextDueOdometer());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(11, _tmp);
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindLong(14, entity.getUpdatedAt());
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateAfterLog = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE maintenance_tasks\n"
                + "        SET lastPerformedDate = ?,\n"
                + "            lastPerformedOdometer = ?,\n"
                + "            nextDueDate = ?,\n"
                + "            nextDueOdometer = ?,\n"
                + "            updatedAt = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfDeactivate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE maintenance_tasks SET isActive = 0, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MaintenanceTaskEntity task,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMaintenanceTaskEntity.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MaintenanceTaskEntity task,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMaintenanceTaskEntity.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAfterLog(final long id, final long date, final int odometer,
      final Long nextDate, final Integer nextOdometer, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAfterLog.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, date);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, odometer);
        _argIndex = 3;
        if (nextDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, nextDate);
        }
        _argIndex = 4;
        if (nextOdometer == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, nextOdometer);
        }
        _argIndex = 5;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateAfterLog.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivate(final long id, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivate.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeactivate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MaintenanceTaskEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM maintenance_tasks WHERE vehicleId = ? AND isActive = 1 ORDER BY CASE WHEN nextDueDate IS NULL THEN 1 ELSE 0 END, nextDueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"maintenance_tasks"}, new Callable<List<MaintenanceTaskEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MaintenanceTaskEntity> _result = new ArrayList<MaintenanceTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<MaintenanceTaskEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM maintenance_tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"maintenance_tasks"}, new Callable<MaintenanceTaskEntity>() {
      @Override
      @Nullable
      public MaintenanceTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final MaintenanceTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object getById(final long id,
      final Continuation<? super MaintenanceTaskEntity> $completion) {
    final String _sql = "SELECT * FROM maintenance_tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MaintenanceTaskEntity>() {
      @Override
      @Nullable
      public MaintenanceTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final MaintenanceTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getActiveByVehicle(final long vehicleId,
      final Continuation<? super List<MaintenanceTaskEntity>> $completion) {
    final String _sql = "SELECT * FROM maintenance_tasks WHERE vehicleId = ? AND isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MaintenanceTaskEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MaintenanceTaskEntity> _result = new ArrayList<MaintenanceTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllActive(final Continuation<? super List<MaintenanceTaskEntity>> $completion) {
    final String _sql = "SELECT * FROM maintenance_tasks WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MaintenanceTaskEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MaintenanceTaskEntity> _result = new ArrayList<MaintenanceTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getOverdueTasks(final long nowMillis,
      final Continuation<? super List<MaintenanceTaskEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT mt.* FROM maintenance_tasks mt\n"
            + "        JOIN vehicles v ON mt.vehicleId = v.id\n"
            + "        WHERE mt.isActive = 1\n"
            + "        AND (\n"
            + "            (mt.nextDueDate IS NOT NULL AND mt.nextDueDate < ?)\n"
            + "            OR\n"
            + "            (mt.nextDueOdometer IS NOT NULL AND mt.nextDueOdometer <= v.currentOdometer)\n"
            + "        )\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, nowMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MaintenanceTaskEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MaintenanceTaskEntity> _result = new ArrayList<MaintenanceTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getTasksDueSoon(final long nowMillis, final long soonMillis, final int kmBuffer,
      final Continuation<? super List<MaintenanceTaskEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT mt.* FROM maintenance_tasks mt\n"
            + "        JOIN vehicles v ON mt.vehicleId = v.id\n"
            + "        WHERE mt.isActive = 1\n"
            + "        AND (\n"
            + "            (mt.nextDueDate IS NOT NULL AND mt.nextDueDate BETWEEN ? AND ?)\n"
            + "            OR\n"
            + "            (mt.nextDueOdometer IS NOT NULL AND mt.nextDueOdometer BETWEEN v.currentOdometer AND v.currentOdometer + ?)\n"
            + "        )\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, nowMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, soonMillis);
    _argIndex = 3;
    _statement.bindLong(_argIndex, kmBuffer);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MaintenanceTaskEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfCustomName = CursorUtil.getColumnIndexOrThrow(_cursor, "customName");
          final int _cursorIndexOfThresholdDays = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdDays");
          final int _cursorIndexOfThresholdKm = CursorUtil.getColumnIndexOrThrow(_cursor, "thresholdKm");
          final int _cursorIndexOfLastPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedDate");
          final int _cursorIndexOfLastPerformedOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPerformedOdometer");
          final int _cursorIndexOfNextDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueDate");
          final int _cursorIndexOfNextDueOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "nextDueOdometer");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<MaintenanceTaskEntity> _result = new ArrayList<MaintenanceTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpCustomName;
            if (_cursor.isNull(_cursorIndexOfCustomName)) {
              _tmpCustomName = null;
            } else {
              _tmpCustomName = _cursor.getString(_cursorIndexOfCustomName);
            }
            final Integer _tmpThresholdDays;
            if (_cursor.isNull(_cursorIndexOfThresholdDays)) {
              _tmpThresholdDays = null;
            } else {
              _tmpThresholdDays = _cursor.getInt(_cursorIndexOfThresholdDays);
            }
            final Integer _tmpThresholdKm;
            if (_cursor.isNull(_cursorIndexOfThresholdKm)) {
              _tmpThresholdKm = null;
            } else {
              _tmpThresholdKm = _cursor.getInt(_cursorIndexOfThresholdKm);
            }
            final Long _tmpLastPerformedDate;
            if (_cursor.isNull(_cursorIndexOfLastPerformedDate)) {
              _tmpLastPerformedDate = null;
            } else {
              _tmpLastPerformedDate = _cursor.getLong(_cursorIndexOfLastPerformedDate);
            }
            final Integer _tmpLastPerformedOdometer;
            if (_cursor.isNull(_cursorIndexOfLastPerformedOdometer)) {
              _tmpLastPerformedOdometer = null;
            } else {
              _tmpLastPerformedOdometer = _cursor.getInt(_cursorIndexOfLastPerformedOdometer);
            }
            final Long _tmpNextDueDate;
            if (_cursor.isNull(_cursorIndexOfNextDueDate)) {
              _tmpNextDueDate = null;
            } else {
              _tmpNextDueDate = _cursor.getLong(_cursorIndexOfNextDueDate);
            }
            final Integer _tmpNextDueOdometer;
            if (_cursor.isNull(_cursorIndexOfNextDueOdometer)) {
              _tmpNextDueOdometer = null;
            } else {
              _tmpNextDueOdometer = _cursor.getInt(_cursorIndexOfNextDueOdometer);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new MaintenanceTaskEntity(_tmpId,_tmpVehicleId,_tmpTaskType,_tmpCustomName,_tmpThresholdDays,_tmpThresholdKm,_tmpLastPerformedDate,_tmpLastPerformedOdometer,_tmpNextDueDate,_tmpNextDueOdometer,_tmpIsActive,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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

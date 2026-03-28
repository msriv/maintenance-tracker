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
import com.moto.tracker.data.local.entity.MaintenanceLogEntity;
import java.lang.Class;
import java.lang.Double;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MaintenanceLogDao_Impl implements MaintenanceLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MaintenanceLogEntity> __insertionAdapterOfMaintenanceLogEntity;

  private final EntityDeletionOrUpdateAdapter<MaintenanceLogEntity> __deletionAdapterOfMaintenanceLogEntity;

  public MaintenanceLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMaintenanceLogEntity = new EntityInsertionAdapter<MaintenanceLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `maintenance_logs` (`id`,`taskId`,`vehicleId`,`performedDate`,`odometer`,`cost`,`mechanicName`,`serviceCenterName`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaintenanceLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTaskId());
        statement.bindLong(3, entity.getVehicleId());
        statement.bindLong(4, entity.getPerformedDate());
        statement.bindLong(5, entity.getOdometer());
        if (entity.getCost() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getCost());
        }
        if (entity.getMechanicName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getMechanicName());
        }
        if (entity.getServiceCenterName() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getServiceCenterName());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfMaintenanceLogEntity = new EntityDeletionOrUpdateAdapter<MaintenanceLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `maintenance_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MaintenanceLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MaintenanceLogEntity log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMaintenanceLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MaintenanceLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMaintenanceLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MaintenanceLogEntity>> observeByTask(final long taskId) {
    final String _sql = "SELECT * FROM maintenance_logs WHERE taskId = ? ORDER BY performedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, taskId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"maintenance_logs"}, new Callable<List<MaintenanceLogEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "performedDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfMechanicName = CursorUtil.getColumnIndexOrThrow(_cursor, "mechanicName");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MaintenanceLogEntity> _result = new ArrayList<MaintenanceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTaskId;
            _tmpTaskId = _cursor.getLong(_cursorIndexOfTaskId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpPerformedDate;
            _tmpPerformedDate = _cursor.getLong(_cursorIndexOfPerformedDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final Double _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            }
            final String _tmpMechanicName;
            if (_cursor.isNull(_cursorIndexOfMechanicName)) {
              _tmpMechanicName = null;
            } else {
              _tmpMechanicName = _cursor.getString(_cursorIndexOfMechanicName);
            }
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MaintenanceLogEntity(_tmpId,_tmpTaskId,_tmpVehicleId,_tmpPerformedDate,_tmpOdometer,_tmpCost,_tmpMechanicName,_tmpServiceCenterName,_tmpNotes,_tmpCreatedAt);
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
  public Flow<List<MaintenanceLogEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM maintenance_logs WHERE vehicleId = ? ORDER BY performedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"maintenance_logs"}, new Callable<List<MaintenanceLogEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "performedDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfMechanicName = CursorUtil.getColumnIndexOrThrow(_cursor, "mechanicName");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MaintenanceLogEntity> _result = new ArrayList<MaintenanceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTaskId;
            _tmpTaskId = _cursor.getLong(_cursorIndexOfTaskId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpPerformedDate;
            _tmpPerformedDate = _cursor.getLong(_cursorIndexOfPerformedDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final Double _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            }
            final String _tmpMechanicName;
            if (_cursor.isNull(_cursorIndexOfMechanicName)) {
              _tmpMechanicName = null;
            } else {
              _tmpMechanicName = _cursor.getString(_cursorIndexOfMechanicName);
            }
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MaintenanceLogEntity(_tmpId,_tmpTaskId,_tmpVehicleId,_tmpPerformedDate,_tmpOdometer,_tmpCost,_tmpMechanicName,_tmpServiceCenterName,_tmpNotes,_tmpCreatedAt);
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
  public Object getByVehicleAndDateRange(final long vehicleId, final long from, final long to,
      final Continuation<? super List<MaintenanceLogEntity>> $completion) {
    final String _sql = "SELECT * FROM maintenance_logs WHERE vehicleId = ? AND performedDate BETWEEN ? AND ? ORDER BY performedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MaintenanceLogEntity>>() {
      @Override
      @NonNull
      public List<MaintenanceLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "performedDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfMechanicName = CursorUtil.getColumnIndexOrThrow(_cursor, "mechanicName");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MaintenanceLogEntity> _result = new ArrayList<MaintenanceLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MaintenanceLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTaskId;
            _tmpTaskId = _cursor.getLong(_cursorIndexOfTaskId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpPerformedDate;
            _tmpPerformedDate = _cursor.getLong(_cursorIndexOfPerformedDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final Double _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            }
            final String _tmpMechanicName;
            if (_cursor.isNull(_cursorIndexOfMechanicName)) {
              _tmpMechanicName = null;
            } else {
              _tmpMechanicName = _cursor.getString(_cursorIndexOfMechanicName);
            }
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MaintenanceLogEntity(_tmpId,_tmpTaskId,_tmpVehicleId,_tmpPerformedDate,_tmpOdometer,_tmpCost,_tmpMechanicName,_tmpServiceCenterName,_tmpNotes,_tmpCreatedAt);
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
  public Object getLatestForTask(final long taskId,
      final Continuation<? super MaintenanceLogEntity> $completion) {
    final String _sql = "SELECT * FROM maintenance_logs WHERE taskId = ? ORDER BY performedDate DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, taskId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MaintenanceLogEntity>() {
      @Override
      @Nullable
      public MaintenanceLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfPerformedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "performedDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfMechanicName = CursorUtil.getColumnIndexOrThrow(_cursor, "mechanicName");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final MaintenanceLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTaskId;
            _tmpTaskId = _cursor.getLong(_cursorIndexOfTaskId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpPerformedDate;
            _tmpPerformedDate = _cursor.getLong(_cursorIndexOfPerformedDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final Double _tmpCost;
            if (_cursor.isNull(_cursorIndexOfCost)) {
              _tmpCost = null;
            } else {
              _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            }
            final String _tmpMechanicName;
            if (_cursor.isNull(_cursorIndexOfMechanicName)) {
              _tmpMechanicName = null;
            } else {
              _tmpMechanicName = _cursor.getString(_cursorIndexOfMechanicName);
            }
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new MaintenanceLogEntity(_tmpId,_tmpTaskId,_tmpVehicleId,_tmpPerformedDate,_tmpOdometer,_tmpCost,_tmpMechanicName,_tmpServiceCenterName,_tmpNotes,_tmpCreatedAt);
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

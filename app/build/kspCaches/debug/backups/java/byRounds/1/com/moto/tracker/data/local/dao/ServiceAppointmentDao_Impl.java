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
import com.moto.tracker.data.local.entity.ServiceAppointmentEntity;
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
public final class ServiceAppointmentDao_Impl implements ServiceAppointmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ServiceAppointmentEntity> __insertionAdapterOfServiceAppointmentEntity;

  private final EntityDeletionOrUpdateAdapter<ServiceAppointmentEntity> __deletionAdapterOfServiceAppointmentEntity;

  private final EntityDeletionOrUpdateAdapter<ServiceAppointmentEntity> __updateAdapterOfServiceAppointmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  public ServiceAppointmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfServiceAppointmentEntity = new EntityInsertionAdapter<ServiceAppointmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `service_appointments` (`id`,`vehicleId`,`appointmentDate`,`title`,`serviceCenterName`,`serviceCenterAddress`,`serviceCenterPhone`,`taskType`,`estimatedCost`,`actualCost`,`status`,`reminderEnabled`,`reminderMinutesBefore`,`notes`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceAppointmentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getAppointmentDate());
        statement.bindString(4, entity.getTitle());
        if (entity.getServiceCenterName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getServiceCenterName());
        }
        if (entity.getServiceCenterAddress() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getServiceCenterAddress());
        }
        if (entity.getServiceCenterPhone() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getServiceCenterPhone());
        }
        if (entity.getTaskType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTaskType());
        }
        if (entity.getEstimatedCost() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getEstimatedCost());
        }
        if (entity.getActualCost() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getActualCost());
        }
        statement.bindString(11, entity.getStatus());
        final int _tmp = entity.getReminderEnabled() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindLong(13, entity.getReminderMinutesBefore());
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfServiceAppointmentEntity = new EntityDeletionOrUpdateAdapter<ServiceAppointmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `service_appointments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceAppointmentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfServiceAppointmentEntity = new EntityDeletionOrUpdateAdapter<ServiceAppointmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `service_appointments` SET `id` = ?,`vehicleId` = ?,`appointmentDate` = ?,`title` = ?,`serviceCenterName` = ?,`serviceCenterAddress` = ?,`serviceCenterPhone` = ?,`taskType` = ?,`estimatedCost` = ?,`actualCost` = ?,`status` = ?,`reminderEnabled` = ?,`reminderMinutesBefore` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceAppointmentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getAppointmentDate());
        statement.bindString(4, entity.getTitle());
        if (entity.getServiceCenterName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getServiceCenterName());
        }
        if (entity.getServiceCenterAddress() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getServiceCenterAddress());
        }
        if (entity.getServiceCenterPhone() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getServiceCenterPhone());
        }
        if (entity.getTaskType() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getTaskType());
        }
        if (entity.getEstimatedCost() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getEstimatedCost());
        }
        if (entity.getActualCost() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getActualCost());
        }
        statement.bindString(11, entity.getStatus());
        final int _tmp = entity.getReminderEnabled() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindLong(13, entity.getReminderMinutesBefore());
        if (entity.getNotes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getNotes());
        }
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE service_appointments SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ServiceAppointmentEntity appointment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfServiceAppointmentEntity.insertAndReturnId(appointment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ServiceAppointmentEntity appointment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfServiceAppointmentEntity.handle(appointment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ServiceAppointmentEntity appointment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfServiceAppointmentEntity.handle(appointment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long id, final String status, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
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
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ServiceAppointmentEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM service_appointments WHERE vehicleId = ? ORDER BY appointmentDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"service_appointments"}, new Callable<List<ServiceAppointmentEntity>>() {
      @Override
      @NonNull
      public List<ServiceAppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfServiceCenterAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterAddress");
          final int _cursorIndexOfServiceCenterPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterPhone");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfEstimatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedCost");
          final int _cursorIndexOfActualCost = CursorUtil.getColumnIndexOrThrow(_cursor, "actualCost");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderEnabled");
          final int _cursorIndexOfReminderMinutesBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutesBefore");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceAppointmentEntity> _result = new ArrayList<ServiceAppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceAppointmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getLong(_cursorIndexOfAppointmentDate);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpServiceCenterAddress;
            if (_cursor.isNull(_cursorIndexOfServiceCenterAddress)) {
              _tmpServiceCenterAddress = null;
            } else {
              _tmpServiceCenterAddress = _cursor.getString(_cursorIndexOfServiceCenterAddress);
            }
            final String _tmpServiceCenterPhone;
            if (_cursor.isNull(_cursorIndexOfServiceCenterPhone)) {
              _tmpServiceCenterPhone = null;
            } else {
              _tmpServiceCenterPhone = _cursor.getString(_cursorIndexOfServiceCenterPhone);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            final Double _tmpEstimatedCost;
            if (_cursor.isNull(_cursorIndexOfEstimatedCost)) {
              _tmpEstimatedCost = null;
            } else {
              _tmpEstimatedCost = _cursor.getDouble(_cursorIndexOfEstimatedCost);
            }
            final Double _tmpActualCost;
            if (_cursor.isNull(_cursorIndexOfActualCost)) {
              _tmpActualCost = null;
            } else {
              _tmpActualCost = _cursor.getDouble(_cursorIndexOfActualCost);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpReminderEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp != 0;
            final int _tmpReminderMinutesBefore;
            _tmpReminderMinutesBefore = _cursor.getInt(_cursorIndexOfReminderMinutesBefore);
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
            _item = new ServiceAppointmentEntity(_tmpId,_tmpVehicleId,_tmpAppointmentDate,_tmpTitle,_tmpServiceCenterName,_tmpServiceCenterAddress,_tmpServiceCenterPhone,_tmpTaskType,_tmpEstimatedCost,_tmpActualCost,_tmpStatus,_tmpReminderEnabled,_tmpReminderMinutesBefore,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getById(final long id,
      final Continuation<? super ServiceAppointmentEntity> $completion) {
    final String _sql = "SELECT * FROM service_appointments WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ServiceAppointmentEntity>() {
      @Override
      @Nullable
      public ServiceAppointmentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfServiceCenterAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterAddress");
          final int _cursorIndexOfServiceCenterPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterPhone");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfEstimatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedCost");
          final int _cursorIndexOfActualCost = CursorUtil.getColumnIndexOrThrow(_cursor, "actualCost");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderEnabled");
          final int _cursorIndexOfReminderMinutesBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutesBefore");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ServiceAppointmentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getLong(_cursorIndexOfAppointmentDate);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpServiceCenterAddress;
            if (_cursor.isNull(_cursorIndexOfServiceCenterAddress)) {
              _tmpServiceCenterAddress = null;
            } else {
              _tmpServiceCenterAddress = _cursor.getString(_cursorIndexOfServiceCenterAddress);
            }
            final String _tmpServiceCenterPhone;
            if (_cursor.isNull(_cursorIndexOfServiceCenterPhone)) {
              _tmpServiceCenterPhone = null;
            } else {
              _tmpServiceCenterPhone = _cursor.getString(_cursorIndexOfServiceCenterPhone);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            final Double _tmpEstimatedCost;
            if (_cursor.isNull(_cursorIndexOfEstimatedCost)) {
              _tmpEstimatedCost = null;
            } else {
              _tmpEstimatedCost = _cursor.getDouble(_cursorIndexOfEstimatedCost);
            }
            final Double _tmpActualCost;
            if (_cursor.isNull(_cursorIndexOfActualCost)) {
              _tmpActualCost = null;
            } else {
              _tmpActualCost = _cursor.getDouble(_cursorIndexOfActualCost);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpReminderEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp != 0;
            final int _tmpReminderMinutesBefore;
            _tmpReminderMinutesBefore = _cursor.getInt(_cursorIndexOfReminderMinutesBefore);
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
            _result = new ServiceAppointmentEntity(_tmpId,_tmpVehicleId,_tmpAppointmentDate,_tmpTitle,_tmpServiceCenterName,_tmpServiceCenterAddress,_tmpServiceCenterPhone,_tmpTaskType,_tmpEstimatedCost,_tmpActualCost,_tmpStatus,_tmpReminderEnabled,_tmpReminderMinutesBefore,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getUpcomingAppointments(final long now,
      final Continuation<? super List<ServiceAppointmentEntity>> $completion) {
    final String _sql = "SELECT * FROM service_appointments WHERE appointmentDate >= ? AND status = 'upcoming' ORDER BY appointmentDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceAppointmentEntity>>() {
      @Override
      @NonNull
      public List<ServiceAppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfServiceCenterAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterAddress");
          final int _cursorIndexOfServiceCenterPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterPhone");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfEstimatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedCost");
          final int _cursorIndexOfActualCost = CursorUtil.getColumnIndexOrThrow(_cursor, "actualCost");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderEnabled");
          final int _cursorIndexOfReminderMinutesBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutesBefore");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceAppointmentEntity> _result = new ArrayList<ServiceAppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceAppointmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getLong(_cursorIndexOfAppointmentDate);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpServiceCenterAddress;
            if (_cursor.isNull(_cursorIndexOfServiceCenterAddress)) {
              _tmpServiceCenterAddress = null;
            } else {
              _tmpServiceCenterAddress = _cursor.getString(_cursorIndexOfServiceCenterAddress);
            }
            final String _tmpServiceCenterPhone;
            if (_cursor.isNull(_cursorIndexOfServiceCenterPhone)) {
              _tmpServiceCenterPhone = null;
            } else {
              _tmpServiceCenterPhone = _cursor.getString(_cursorIndexOfServiceCenterPhone);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            final Double _tmpEstimatedCost;
            if (_cursor.isNull(_cursorIndexOfEstimatedCost)) {
              _tmpEstimatedCost = null;
            } else {
              _tmpEstimatedCost = _cursor.getDouble(_cursorIndexOfEstimatedCost);
            }
            final Double _tmpActualCost;
            if (_cursor.isNull(_cursorIndexOfActualCost)) {
              _tmpActualCost = null;
            } else {
              _tmpActualCost = _cursor.getDouble(_cursorIndexOfActualCost);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpReminderEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp != 0;
            final int _tmpReminderMinutesBefore;
            _tmpReminderMinutesBefore = _cursor.getInt(_cursorIndexOfReminderMinutesBefore);
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
            _item = new ServiceAppointmentEntity(_tmpId,_tmpVehicleId,_tmpAppointmentDate,_tmpTitle,_tmpServiceCenterName,_tmpServiceCenterAddress,_tmpServiceCenterPhone,_tmpTaskType,_tmpEstimatedCost,_tmpActualCost,_tmpStatus,_tmpReminderEnabled,_tmpReminderMinutesBefore,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAppointmentsWithReminders(final long now,
      final Continuation<? super List<ServiceAppointmentEntity>> $completion) {
    final String _sql = "SELECT * FROM service_appointments WHERE reminderEnabled = 1 AND status = 'upcoming' AND appointmentDate > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ServiceAppointmentEntity>>() {
      @Override
      @NonNull
      public List<ServiceAppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfServiceCenterName = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterName");
          final int _cursorIndexOfServiceCenterAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterAddress");
          final int _cursorIndexOfServiceCenterPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "serviceCenterPhone");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfEstimatedCost = CursorUtil.getColumnIndexOrThrow(_cursor, "estimatedCost");
          final int _cursorIndexOfActualCost = CursorUtil.getColumnIndexOrThrow(_cursor, "actualCost");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReminderEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderEnabled");
          final int _cursorIndexOfReminderMinutesBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutesBefore");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ServiceAppointmentEntity> _result = new ArrayList<ServiceAppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ServiceAppointmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getLong(_cursorIndexOfAppointmentDate);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpServiceCenterName;
            if (_cursor.isNull(_cursorIndexOfServiceCenterName)) {
              _tmpServiceCenterName = null;
            } else {
              _tmpServiceCenterName = _cursor.getString(_cursorIndexOfServiceCenterName);
            }
            final String _tmpServiceCenterAddress;
            if (_cursor.isNull(_cursorIndexOfServiceCenterAddress)) {
              _tmpServiceCenterAddress = null;
            } else {
              _tmpServiceCenterAddress = _cursor.getString(_cursorIndexOfServiceCenterAddress);
            }
            final String _tmpServiceCenterPhone;
            if (_cursor.isNull(_cursorIndexOfServiceCenterPhone)) {
              _tmpServiceCenterPhone = null;
            } else {
              _tmpServiceCenterPhone = _cursor.getString(_cursorIndexOfServiceCenterPhone);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            final Double _tmpEstimatedCost;
            if (_cursor.isNull(_cursorIndexOfEstimatedCost)) {
              _tmpEstimatedCost = null;
            } else {
              _tmpEstimatedCost = _cursor.getDouble(_cursorIndexOfEstimatedCost);
            }
            final Double _tmpActualCost;
            if (_cursor.isNull(_cursorIndexOfActualCost)) {
              _tmpActualCost = null;
            } else {
              _tmpActualCost = _cursor.getDouble(_cursorIndexOfActualCost);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final boolean _tmpReminderEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfReminderEnabled);
            _tmpReminderEnabled = _tmp != 0;
            final int _tmpReminderMinutesBefore;
            _tmpReminderMinutesBefore = _cursor.getInt(_cursorIndexOfReminderMinutesBefore);
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
            _item = new ServiceAppointmentEntity(_tmpId,_tmpVehicleId,_tmpAppointmentDate,_tmpTitle,_tmpServiceCenterName,_tmpServiceCenterAddress,_tmpServiceCenterPhone,_tmpTaskType,_tmpEstimatedCost,_tmpActualCost,_tmpStatus,_tmpReminderEnabled,_tmpReminderMinutesBefore,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt);
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

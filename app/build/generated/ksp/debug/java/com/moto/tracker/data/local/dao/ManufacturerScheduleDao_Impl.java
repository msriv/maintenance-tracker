package com.moto.tracker.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.moto.tracker.data.local.entity.ManufacturerScheduleEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ManufacturerScheduleDao_Impl implements ManufacturerScheduleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ManufacturerScheduleEntity> __insertionAdapterOfManufacturerScheduleEntity;

  public ManufacturerScheduleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfManufacturerScheduleEntity = new EntityInsertionAdapter<ManufacturerScheduleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `manufacturer_schedules` (`id`,`make`,`model`,`yearFrom`,`yearTo`,`taskType`,`taskLabel`,`intervalDays`,`intervalKm`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ManufacturerScheduleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMake());
        statement.bindString(3, entity.getModel());
        statement.bindLong(4, entity.getYearFrom());
        if (entity.getYearTo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getYearTo());
        }
        statement.bindString(6, entity.getTaskType());
        statement.bindString(7, entity.getTaskLabel());
        if (entity.getIntervalDays() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getIntervalDays());
        }
        if (entity.getIntervalKm() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getIntervalKm());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
      }
    };
  }

  @Override
  public Object insertAll(final List<ManufacturerScheduleEntity> schedules,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfManufacturerScheduleEntity.insert(schedules);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDistinctMakes(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT make FROM manufacturer_schedules ORDER BY make ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getModelsForMake(final String make,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT model FROM manufacturer_schedules WHERE make = ? ORDER BY model ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, make);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Flow<List<ManufacturerScheduleEntity>> observeScheduleForVehicle(final String make,
      final String model, final int year) {
    final String _sql = "\n"
            + "        SELECT * FROM manufacturer_schedules\n"
            + "        WHERE make = ? AND model = ?\n"
            + "        AND yearFrom <= ?\n"
            + "        AND (yearTo IS NULL OR yearTo >= ?)\n"
            + "        ORDER BY taskLabel ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, make);
    _argIndex = 2;
    _statement.bindString(_argIndex, model);
    _argIndex = 3;
    _statement.bindLong(_argIndex, year);
    _argIndex = 4;
    _statement.bindLong(_argIndex, year);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"manufacturer_schedules"}, new Callable<List<ManufacturerScheduleEntity>>() {
      @Override
      @NonNull
      public List<ManufacturerScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMake = CursorUtil.getColumnIndexOrThrow(_cursor, "make");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYearFrom = CursorUtil.getColumnIndexOrThrow(_cursor, "yearFrom");
          final int _cursorIndexOfYearTo = CursorUtil.getColumnIndexOrThrow(_cursor, "yearTo");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final int _cursorIndexOfTaskLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "taskLabel");
          final int _cursorIndexOfIntervalDays = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalDays");
          final int _cursorIndexOfIntervalKm = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalKm");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<ManufacturerScheduleEntity> _result = new ArrayList<ManufacturerScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManufacturerScheduleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMake;
            _tmpMake = _cursor.getString(_cursorIndexOfMake);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYearFrom;
            _tmpYearFrom = _cursor.getInt(_cursorIndexOfYearFrom);
            final Integer _tmpYearTo;
            if (_cursor.isNull(_cursorIndexOfYearTo)) {
              _tmpYearTo = null;
            } else {
              _tmpYearTo = _cursor.getInt(_cursorIndexOfYearTo);
            }
            final String _tmpTaskType;
            _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            final String _tmpTaskLabel;
            _tmpTaskLabel = _cursor.getString(_cursorIndexOfTaskLabel);
            final Integer _tmpIntervalDays;
            if (_cursor.isNull(_cursorIndexOfIntervalDays)) {
              _tmpIntervalDays = null;
            } else {
              _tmpIntervalDays = _cursor.getInt(_cursorIndexOfIntervalDays);
            }
            final Integer _tmpIntervalKm;
            if (_cursor.isNull(_cursorIndexOfIntervalKm)) {
              _tmpIntervalKm = null;
            } else {
              _tmpIntervalKm = _cursor.getInt(_cursorIndexOfIntervalKm);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new ManufacturerScheduleEntity(_tmpId,_tmpMake,_tmpModel,_tmpYearFrom,_tmpYearTo,_tmpTaskType,_tmpTaskLabel,_tmpIntervalDays,_tmpIntervalKm,_tmpNotes);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM manufacturer_schedules";
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

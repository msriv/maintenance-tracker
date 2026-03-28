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
import com.moto.tracker.data.local.entity.FuelLogEntity;
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
public final class FuelLogDao_Impl implements FuelLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FuelLogEntity> __insertionAdapterOfFuelLogEntity;

  private final EntityDeletionOrUpdateAdapter<FuelLogEntity> __deletionAdapterOfFuelLogEntity;

  private final EntityDeletionOrUpdateAdapter<FuelLogEntity> __updateAdapterOfFuelLogEntity;

  public FuelLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFuelLogEntity = new EntityInsertionAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `fuel_logs` (`id`,`vehicleId`,`fillDate`,`odometer`,`liters`,`pricePerLiter`,`totalCost`,`fuelType`,`stationName`,`isTankFull`,`kmPerLiter`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getFillDate());
        statement.bindLong(4, entity.getOdometer());
        statement.bindDouble(5, entity.getLiters());
        statement.bindDouble(6, entity.getPricePerLiter());
        statement.bindDouble(7, entity.getTotalCost());
        statement.bindString(8, entity.getFuelType());
        if (entity.getStationName() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getStationName());
        }
        final int _tmp = entity.isTankFull() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getKmPerLiter() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getKmPerLiter());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfFuelLogEntity = new EntityDeletionOrUpdateAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `fuel_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFuelLogEntity = new EntityDeletionOrUpdateAdapter<FuelLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `fuel_logs` SET `id` = ?,`vehicleId` = ?,`fillDate` = ?,`odometer` = ?,`liters` = ?,`pricePerLiter` = ?,`totalCost` = ?,`fuelType` = ?,`stationName` = ?,`isTankFull` = ?,`kmPerLiter` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FuelLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindLong(3, entity.getFillDate());
        statement.bindLong(4, entity.getOdometer());
        statement.bindDouble(5, entity.getLiters());
        statement.bindDouble(6, entity.getPricePerLiter());
        statement.bindDouble(7, entity.getTotalCost());
        statement.bindString(8, entity.getFuelType());
        if (entity.getStationName() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getStationName());
        }
        final int _tmp = entity.isTankFull() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getKmPerLiter() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getKmPerLiter());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindLong(14, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final FuelLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFuelLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FuelLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFuelLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FuelLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFuelLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FuelLogEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM fuel_logs WHERE vehicleId = ? ORDER BY fillDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fuel_logs"}, new Callable<List<FuelLogEntity>>() {
      @Override
      @NonNull
      public List<FuelLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfFillDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fillDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfIsTankFull = CursorUtil.getColumnIndexOrThrow(_cursor, "isTankFull");
          final int _cursorIndexOfKmPerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "kmPerLiter");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FuelLogEntity> _result = new ArrayList<FuelLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuelLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpFillDate;
            _tmpFillDate = _cursor.getLong(_cursorIndexOfFillDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final boolean _tmpIsTankFull;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTankFull);
            _tmpIsTankFull = _tmp != 0;
            final Double _tmpKmPerLiter;
            if (_cursor.isNull(_cursorIndexOfKmPerLiter)) {
              _tmpKmPerLiter = null;
            } else {
              _tmpKmPerLiter = _cursor.getDouble(_cursorIndexOfKmPerLiter);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpFillDate,_tmpOdometer,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpFuelType,_tmpStationName,_tmpIsTankFull,_tmpKmPerLiter,_tmpNotes,_tmpCreatedAt);
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
  public Object getById(final long id, final Continuation<? super FuelLogEntity> $completion) {
    final String _sql = "SELECT * FROM fuel_logs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FuelLogEntity>() {
      @Override
      @Nullable
      public FuelLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfFillDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fillDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfIsTankFull = CursorUtil.getColumnIndexOrThrow(_cursor, "isTankFull");
          final int _cursorIndexOfKmPerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "kmPerLiter");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final FuelLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpFillDate;
            _tmpFillDate = _cursor.getLong(_cursorIndexOfFillDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final boolean _tmpIsTankFull;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTankFull);
            _tmpIsTankFull = _tmp != 0;
            final Double _tmpKmPerLiter;
            if (_cursor.isNull(_cursorIndexOfKmPerLiter)) {
              _tmpKmPerLiter = null;
            } else {
              _tmpKmPerLiter = _cursor.getDouble(_cursorIndexOfKmPerLiter);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpFillDate,_tmpOdometer,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpFuelType,_tmpStationName,_tmpIsTankFull,_tmpKmPerLiter,_tmpNotes,_tmpCreatedAt);
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
  public Object getByVehicleAndDateRange(final long vehicleId, final long from, final long to,
      final Continuation<? super List<FuelLogEntity>> $completion) {
    final String _sql = "SELECT * FROM fuel_logs WHERE vehicleId = ? AND fillDate BETWEEN ? AND ? ORDER BY fillDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FuelLogEntity>>() {
      @Override
      @NonNull
      public List<FuelLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfFillDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fillDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfIsTankFull = CursorUtil.getColumnIndexOrThrow(_cursor, "isTankFull");
          final int _cursorIndexOfKmPerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "kmPerLiter");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FuelLogEntity> _result = new ArrayList<FuelLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuelLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpFillDate;
            _tmpFillDate = _cursor.getLong(_cursorIndexOfFillDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final boolean _tmpIsTankFull;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTankFull);
            _tmpIsTankFull = _tmp != 0;
            final Double _tmpKmPerLiter;
            if (_cursor.isNull(_cursorIndexOfKmPerLiter)) {
              _tmpKmPerLiter = null;
            } else {
              _tmpKmPerLiter = _cursor.getDouble(_cursorIndexOfKmPerLiter);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpFillDate,_tmpOdometer,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpFuelType,_tmpStationName,_tmpIsTankFull,_tmpKmPerLiter,_tmpNotes,_tmpCreatedAt);
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
  public Object getLastFullFillUps(final long vehicleId, final int limit,
      final Continuation<? super List<FuelLogEntity>> $completion) {
    final String _sql = "SELECT * FROM fuel_logs WHERE vehicleId = ? AND isTankFull = 1 ORDER BY fillDate DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FuelLogEntity>>() {
      @Override
      @NonNull
      public List<FuelLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfFillDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fillDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfIsTankFull = CursorUtil.getColumnIndexOrThrow(_cursor, "isTankFull");
          final int _cursorIndexOfKmPerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "kmPerLiter");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FuelLogEntity> _result = new ArrayList<FuelLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FuelLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpFillDate;
            _tmpFillDate = _cursor.getLong(_cursorIndexOfFillDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final boolean _tmpIsTankFull;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTankFull);
            _tmpIsTankFull = _tmp != 0;
            final Double _tmpKmPerLiter;
            if (_cursor.isNull(_cursorIndexOfKmPerLiter)) {
              _tmpKmPerLiter = null;
            } else {
              _tmpKmPerLiter = _cursor.getDouble(_cursorIndexOfKmPerLiter);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpFillDate,_tmpOdometer,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpFuelType,_tmpStationName,_tmpIsTankFull,_tmpKmPerLiter,_tmpNotes,_tmpCreatedAt);
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
  public Object getAverageKmPerLiter(final long vehicleId,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(kmPerLiter) FROM fuel_logs WHERE vehicleId = ? AND kmPerLiter IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
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
  public Object getLatestByVehicle(final long vehicleId,
      final Continuation<? super FuelLogEntity> $completion) {
    final String _sql = "SELECT * FROM fuel_logs WHERE vehicleId = ? ORDER BY fillDate DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FuelLogEntity>() {
      @Override
      @Nullable
      public FuelLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfFillDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fillDate");
          final int _cursorIndexOfOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "odometer");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfTotalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCost");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "stationName");
          final int _cursorIndexOfIsTankFull = CursorUtil.getColumnIndexOrThrow(_cursor, "isTankFull");
          final int _cursorIndexOfKmPerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "kmPerLiter");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final FuelLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final long _tmpFillDate;
            _tmpFillDate = _cursor.getLong(_cursorIndexOfFillDate);
            final int _tmpOdometer;
            _tmpOdometer = _cursor.getInt(_cursorIndexOfOdometer);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final double _tmpTotalCost;
            _tmpTotalCost = _cursor.getDouble(_cursorIndexOfTotalCost);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpStationName;
            if (_cursor.isNull(_cursorIndexOfStationName)) {
              _tmpStationName = null;
            } else {
              _tmpStationName = _cursor.getString(_cursorIndexOfStationName);
            }
            final boolean _tmpIsTankFull;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTankFull);
            _tmpIsTankFull = _tmp != 0;
            final Double _tmpKmPerLiter;
            if (_cursor.isNull(_cursorIndexOfKmPerLiter)) {
              _tmpKmPerLiter = null;
            } else {
              _tmpKmPerLiter = _cursor.getDouble(_cursorIndexOfKmPerLiter);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new FuelLogEntity(_tmpId,_tmpVehicleId,_tmpFillDate,_tmpOdometer,_tmpLiters,_tmpPricePerLiter,_tmpTotalCost,_tmpFuelType,_tmpStationName,_tmpIsTankFull,_tmpKmPerLiter,_tmpNotes,_tmpCreatedAt);
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

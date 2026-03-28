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
import com.moto.tracker.data.local.entity.VehicleEntity;
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
public final class VehicleDao_Impl implements VehicleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VehicleEntity> __insertionAdapterOfVehicleEntity;

  private final EntityDeletionOrUpdateAdapter<VehicleEntity> __deletionAdapterOfVehicleEntity;

  private final EntityDeletionOrUpdateAdapter<VehicleEntity> __updateAdapterOfVehicleEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateOdometer;

  public VehicleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVehicleEntity = new EntityInsertionAdapter<VehicleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vehicles` (`id`,`nickname`,`make`,`model`,`year`,`type`,`vin`,`licensePlate`,`currentOdometer`,`fuelType`,`color`,`purchaseDate`,`imageUri`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNickname());
        statement.bindString(3, entity.getMake());
        statement.bindString(4, entity.getModel());
        statement.bindLong(5, entity.getYear());
        statement.bindString(6, entity.getType());
        if (entity.getVin() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getVin());
        }
        if (entity.getLicensePlate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLicensePlate());
        }
        statement.bindLong(9, entity.getCurrentOdometer());
        statement.bindString(10, entity.getFuelType());
        if (entity.getColor() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getColor());
        }
        if (entity.getPurchaseDate() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getPurchaseDate());
        }
        if (entity.getImageUri() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getImageUri());
        }
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfVehicleEntity = new EntityDeletionOrUpdateAdapter<VehicleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `vehicles` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVehicleEntity = new EntityDeletionOrUpdateAdapter<VehicleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `vehicles` SET `id` = ?,`nickname` = ?,`make` = ?,`model` = ?,`year` = ?,`type` = ?,`vin` = ?,`licensePlate` = ?,`currentOdometer` = ?,`fuelType` = ?,`color` = ?,`purchaseDate` = ?,`imageUri` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehicleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNickname());
        statement.bindString(3, entity.getMake());
        statement.bindString(4, entity.getModel());
        statement.bindLong(5, entity.getYear());
        statement.bindString(6, entity.getType());
        if (entity.getVin() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getVin());
        }
        if (entity.getLicensePlate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLicensePlate());
        }
        statement.bindLong(9, entity.getCurrentOdometer());
        statement.bindString(10, entity.getFuelType());
        if (entity.getColor() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getColor());
        }
        if (entity.getPurchaseDate() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getPurchaseDate());
        }
        if (entity.getImageUri() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getImageUri());
        }
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
        statement.bindLong(16, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateOdometer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vehicles SET currentOdometer = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final VehicleEntity vehicle, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVehicleEntity.insertAndReturnId(vehicle);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final VehicleEntity vehicle, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVehicleEntity.handle(vehicle);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final VehicleEntity vehicle, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVehicleEntity.handle(vehicle);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOdometer(final long id, final int odometer, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateOdometer.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, odometer);
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
          __preparedStmtOfUpdateOdometer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VehicleEntity>> observeAll() {
    final String _sql = "SELECT * FROM vehicles ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicles"}, new Callable<List<VehicleEntity>>() {
      @Override
      @NonNull
      public List<VehicleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfMake = CursorUtil.getColumnIndexOrThrow(_cursor, "make");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfVin = CursorUtil.getColumnIndexOrThrow(_cursor, "vin");
          final int _cursorIndexOfLicensePlate = CursorUtil.getColumnIndexOrThrow(_cursor, "licensePlate");
          final int _cursorIndexOfCurrentOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOdometer");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehicleEntity> _result = new ArrayList<VehicleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpMake;
            _tmpMake = _cursor.getString(_cursorIndexOfMake);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpVin;
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null;
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin);
            }
            final String _tmpLicensePlate;
            if (_cursor.isNull(_cursorIndexOfLicensePlate)) {
              _tmpLicensePlate = null;
            } else {
              _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate);
            }
            final int _tmpCurrentOdometer;
            _tmpCurrentOdometer = _cursor.getInt(_cursorIndexOfCurrentOdometer);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final Long _tmpPurchaseDate;
            if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
              _tmpPurchaseDate = null;
            } else {
              _tmpPurchaseDate = _cursor.getLong(_cursorIndexOfPurchaseDate);
            }
            final String _tmpImageUri;
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _tmpImageUri = null;
            } else {
              _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehicleEntity(_tmpId,_tmpNickname,_tmpMake,_tmpModel,_tmpYear,_tmpType,_tmpVin,_tmpLicensePlate,_tmpCurrentOdometer,_tmpFuelType,_tmpColor,_tmpPurchaseDate,_tmpImageUri,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<VehicleEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM vehicles WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicles"}, new Callable<VehicleEntity>() {
      @Override
      @Nullable
      public VehicleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfMake = CursorUtil.getColumnIndexOrThrow(_cursor, "make");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfVin = CursorUtil.getColumnIndexOrThrow(_cursor, "vin");
          final int _cursorIndexOfLicensePlate = CursorUtil.getColumnIndexOrThrow(_cursor, "licensePlate");
          final int _cursorIndexOfCurrentOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOdometer");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final VehicleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpMake;
            _tmpMake = _cursor.getString(_cursorIndexOfMake);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpVin;
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null;
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin);
            }
            final String _tmpLicensePlate;
            if (_cursor.isNull(_cursorIndexOfLicensePlate)) {
              _tmpLicensePlate = null;
            } else {
              _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate);
            }
            final int _tmpCurrentOdometer;
            _tmpCurrentOdometer = _cursor.getInt(_cursorIndexOfCurrentOdometer);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final Long _tmpPurchaseDate;
            if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
              _tmpPurchaseDate = null;
            } else {
              _tmpPurchaseDate = _cursor.getLong(_cursorIndexOfPurchaseDate);
            }
            final String _tmpImageUri;
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _tmpImageUri = null;
            } else {
              _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new VehicleEntity(_tmpId,_tmpNickname,_tmpMake,_tmpModel,_tmpYear,_tmpType,_tmpVin,_tmpLicensePlate,_tmpCurrentOdometer,_tmpFuelType,_tmpColor,_tmpPurchaseDate,_tmpImageUri,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getById(final long id, final Continuation<? super VehicleEntity> $completion) {
    final String _sql = "SELECT * FROM vehicles WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VehicleEntity>() {
      @Override
      @Nullable
      public VehicleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfMake = CursorUtil.getColumnIndexOrThrow(_cursor, "make");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfVin = CursorUtil.getColumnIndexOrThrow(_cursor, "vin");
          final int _cursorIndexOfLicensePlate = CursorUtil.getColumnIndexOrThrow(_cursor, "licensePlate");
          final int _cursorIndexOfCurrentOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOdometer");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final VehicleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpMake;
            _tmpMake = _cursor.getString(_cursorIndexOfMake);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpVin;
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null;
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin);
            }
            final String _tmpLicensePlate;
            if (_cursor.isNull(_cursorIndexOfLicensePlate)) {
              _tmpLicensePlate = null;
            } else {
              _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate);
            }
            final int _tmpCurrentOdometer;
            _tmpCurrentOdometer = _cursor.getInt(_cursorIndexOfCurrentOdometer);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final Long _tmpPurchaseDate;
            if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
              _tmpPurchaseDate = null;
            } else {
              _tmpPurchaseDate = _cursor.getLong(_cursorIndexOfPurchaseDate);
            }
            final String _tmpImageUri;
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _tmpImageUri = null;
            } else {
              _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new VehicleEntity(_tmpId,_tmpNickname,_tmpMake,_tmpModel,_tmpYear,_tmpType,_tmpVin,_tmpLicensePlate,_tmpCurrentOdometer,_tmpFuelType,_tmpColor,_tmpPurchaseDate,_tmpImageUri,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAll(final Continuation<? super List<VehicleEntity>> $completion) {
    final String _sql = "SELECT * FROM vehicles";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VehicleEntity>>() {
      @Override
      @NonNull
      public List<VehicleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNickname = CursorUtil.getColumnIndexOrThrow(_cursor, "nickname");
          final int _cursorIndexOfMake = CursorUtil.getColumnIndexOrThrow(_cursor, "make");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfVin = CursorUtil.getColumnIndexOrThrow(_cursor, "vin");
          final int _cursorIndexOfLicensePlate = CursorUtil.getColumnIndexOrThrow(_cursor, "licensePlate");
          final int _cursorIndexOfCurrentOdometer = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOdometer");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfPurchaseDate = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseDate");
          final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehicleEntity> _result = new ArrayList<VehicleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehicleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNickname;
            _tmpNickname = _cursor.getString(_cursorIndexOfNickname);
            final String _tmpMake;
            _tmpMake = _cursor.getString(_cursorIndexOfMake);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpVin;
            if (_cursor.isNull(_cursorIndexOfVin)) {
              _tmpVin = null;
            } else {
              _tmpVin = _cursor.getString(_cursorIndexOfVin);
            }
            final String _tmpLicensePlate;
            if (_cursor.isNull(_cursorIndexOfLicensePlate)) {
              _tmpLicensePlate = null;
            } else {
              _tmpLicensePlate = _cursor.getString(_cursorIndexOfLicensePlate);
            }
            final int _tmpCurrentOdometer;
            _tmpCurrentOdometer = _cursor.getInt(_cursorIndexOfCurrentOdometer);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            final Long _tmpPurchaseDate;
            if (_cursor.isNull(_cursorIndexOfPurchaseDate)) {
              _tmpPurchaseDate = null;
            } else {
              _tmpPurchaseDate = _cursor.getLong(_cursorIndexOfPurchaseDate);
            }
            final String _tmpImageUri;
            if (_cursor.isNull(_cursorIndexOfImageUri)) {
              _tmpImageUri = null;
            } else {
              _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehicleEntity(_tmpId,_tmpNickname,_tmpMake,_tmpModel,_tmpYear,_tmpType,_tmpVin,_tmpLicensePlate,_tmpCurrentOdometer,_tmpFuelType,_tmpColor,_tmpPurchaseDate,_tmpImageUri,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM vehicles";
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

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
import com.moto.tracker.data.local.entity.DocumentEntity;
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
public final class DocumentDao_Impl implements DocumentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DocumentEntity> __insertionAdapterOfDocumentEntity;

  private final EntityDeletionOrUpdateAdapter<DocumentEntity> __deletionAdapterOfDocumentEntity;

  private final EntityDeletionOrUpdateAdapter<DocumentEntity> __updateAdapterOfDocumentEntity;

  public DocumentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDocumentEntity = new EntityInsertionAdapter<DocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `documents` (`id`,`vehicleId`,`type`,`title`,`fileUri`,`thumbnailUri`,`expiryDate`,`issueDate`,`notes`,`amount`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DocumentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getTitle());
        if (entity.getFileUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFileUri());
        }
        if (entity.getThumbnailUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getThumbnailUri());
        }
        if (entity.getExpiryDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getExpiryDate());
        }
        if (entity.getIssueDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getIssueDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        if (entity.getAmount() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getAmount());
        }
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfDocumentEntity = new EntityDeletionOrUpdateAdapter<DocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `documents` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DocumentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDocumentEntity = new EntityDeletionOrUpdateAdapter<DocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `documents` SET `id` = ?,`vehicleId` = ?,`type` = ?,`title` = ?,`fileUri` = ?,`thumbnailUri` = ?,`expiryDate` = ?,`issueDate` = ?,`notes` = ?,`amount` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DocumentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getVehicleId());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getTitle());
        if (entity.getFileUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFileUri());
        }
        if (entity.getThumbnailUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getThumbnailUri());
        }
        if (entity.getExpiryDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getExpiryDate());
        }
        if (entity.getIssueDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getIssueDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        if (entity.getAmount() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getAmount());
        }
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getUpdatedAt());
        statement.bindLong(13, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final DocumentEntity document,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDocumentEntity.insertAndReturnId(document);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DocumentEntity document,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDocumentEntity.handle(document);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DocumentEntity document,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDocumentEntity.handle(document);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DocumentEntity>> observeByVehicle(final long vehicleId) {
    final String _sql = "SELECT * FROM documents WHERE vehicleId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"documents"}, new Callable<List<DocumentEntity>>() {
      @Override
      @NonNull
      public List<DocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DocumentEntity> _result = new ArrayList<DocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DocumentEntity>> observeByVehicleAndType(final long vehicleId,
      final String type) {
    final String _sql = "SELECT * FROM documents WHERE vehicleId = ? AND type = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"documents"}, new Callable<List<DocumentEntity>>() {
      @Override
      @NonNull
      public List<DocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DocumentEntity> _result = new ArrayList<DocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getById(final long id, final Continuation<? super DocumentEntity> $completion) {
    final String _sql = "SELECT * FROM documents WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DocumentEntity>() {
      @Override
      @Nullable
      public DocumentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DocumentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getExpiredDocuments(final long timestamp,
      final Continuation<? super List<DocumentEntity>> $completion) {
    final String _sql = "SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DocumentEntity>>() {
      @Override
      @NonNull
      public List<DocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DocumentEntity> _result = new ArrayList<DocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDocumentsExpiringBetween(final long from, final long to,
      final Continuation<? super List<DocumentEntity>> $completion) {
    final String _sql = "SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    _argIndex = 2;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DocumentEntity>>() {
      @Override
      @NonNull
      public List<DocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DocumentEntity> _result = new ArrayList<DocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getExpiringForVehicle(final long vehicleId, final long from, final long to,
      final Continuation<? super List<DocumentEntity>> $completion) {
    final String _sql = "SELECT * FROM documents WHERE vehicleId = ? AND expiryDate IS NOT NULL AND expiryDate BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, vehicleId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, from);
    _argIndex = 3;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DocumentEntity>>() {
      @Override
      @NonNull
      public List<DocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "fileUri");
          final int _cursorIndexOfThumbnailUri = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUri");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfIssueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "issueDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DocumentEntity> _result = new ArrayList<DocumentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DocumentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpVehicleId;
            _tmpVehicleId = _cursor.getLong(_cursorIndexOfVehicleId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpFileUri;
            if (_cursor.isNull(_cursorIndexOfFileUri)) {
              _tmpFileUri = null;
            } else {
              _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            }
            final String _tmpThumbnailUri;
            if (_cursor.isNull(_cursorIndexOfThumbnailUri)) {
              _tmpThumbnailUri = null;
            } else {
              _tmpThumbnailUri = _cursor.getString(_cursorIndexOfThumbnailUri);
            }
            final Long _tmpExpiryDate;
            if (_cursor.isNull(_cursorIndexOfExpiryDate)) {
              _tmpExpiryDate = null;
            } else {
              _tmpExpiryDate = _cursor.getLong(_cursorIndexOfExpiryDate);
            }
            final Long _tmpIssueDate;
            if (_cursor.isNull(_cursorIndexOfIssueDate)) {
              _tmpIssueDate = null;
            } else {
              _tmpIssueDate = _cursor.getLong(_cursorIndexOfIssueDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final Double _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DocumentEntity(_tmpId,_tmpVehicleId,_tmpType,_tmpTitle,_tmpFileUri,_tmpThumbnailUri,_tmpExpiryDate,_tmpIssueDate,_tmpNotes,_tmpAmount,_tmpCreatedAt,_tmpUpdatedAt);
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

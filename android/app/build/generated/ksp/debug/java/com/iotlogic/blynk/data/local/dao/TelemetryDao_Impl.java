package com.iotlogic.blynk.data.local.dao;

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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.iotlogic.blynk.data.local.entities.TelemetryEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class TelemetryDao_Impl implements TelemetryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TelemetryEntity> __insertionAdapterOfTelemetryEntity;

  private final EntityDeletionOrUpdateAdapter<TelemetryEntity> __deletionAdapterOfTelemetryEntity;

  private final EntityDeletionOrUpdateAdapter<TelemetryEntity> __updateAdapterOfTelemetryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsProcessed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTelemetryById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTelemetryByDevice;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldTelemetry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldTelemetryForDevice;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSyncedOldTelemetry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTelemetry;

  public TelemetryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTelemetryEntity = new EntityInsertionAdapter<TelemetryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `telemetry` (`id`,`deviceId`,`sensorType`,`value`,`unit`,`timestamp`,`quality`,`rawValue`,`metadata`,`isProcessed`,`syncStatus`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TelemetryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDeviceId());
        statement.bindString(3, entity.getSensorType());
        statement.bindDouble(4, entity.getValue());
        if (entity.getUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getUnit());
        }
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getQuality() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getQuality());
        }
        if (entity.getRawValue() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getRawValue());
        }
        if (entity.getMetadata() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getMetadata());
        }
        final int _tmp = entity.isProcessed() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindString(11, entity.getSyncStatus());
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfTelemetryEntity = new EntityDeletionOrUpdateAdapter<TelemetryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `telemetry` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TelemetryEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfTelemetryEntity = new EntityDeletionOrUpdateAdapter<TelemetryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `telemetry` SET `id` = ?,`deviceId` = ?,`sensorType` = ?,`value` = ?,`unit` = ?,`timestamp` = ?,`quality` = ?,`rawValue` = ?,`metadata` = ?,`isProcessed` = ?,`syncStatus` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TelemetryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDeviceId());
        statement.bindString(3, entity.getSensorType());
        statement.bindDouble(4, entity.getValue());
        if (entity.getUnit() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getUnit());
        }
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getQuality() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getQuality());
        }
        if (entity.getRawValue() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getRawValue());
        }
        if (entity.getMetadata() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getMetadata());
        }
        final int _tmp = entity.isProcessed() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindString(11, entity.getSyncStatus());
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE telemetry SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsProcessed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE telemetry SET isProcessed = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTelemetryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTelemetryByDevice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry WHERE deviceId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldTelemetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldTelemetryForDevice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry WHERE deviceId = ? AND timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSyncedOldTelemetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry WHERE syncStatus = 'SYNCED' AND timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTelemetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry";
        return _query;
      }
    };
  }

  @Override
  public Object insertTelemetry(final TelemetryEntity telemetry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTelemetryEntity.insert(telemetry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTelemetryBatch(final List<TelemetryEntity> telemetryList,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTelemetryEntity.insert(telemetryList);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTelemetry(final TelemetryEntity telemetry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTelemetryEntity.handle(telemetry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTelemetry(final TelemetryEntity telemetry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTelemetryEntity.handle(telemetry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String telemetryId, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, telemetryId);
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
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsProcessed(final String telemetryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsProcessed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, telemetryId);
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
          __preparedStmtOfMarkAsProcessed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTelemetryById(final String telemetryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTelemetryById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, telemetryId);
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
          __preparedStmtOfDeleteTelemetryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTelemetryByDevice(final String deviceId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTelemetryByDevice.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, deviceId);
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
          __preparedStmtOfDeleteTelemetryByDevice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldTelemetry(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldTelemetry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldTelemetry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldTelemetryForDevice(final String deviceId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldTelemetryForDevice.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, deviceId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldTelemetryForDevice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSyncedOldTelemetry(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSyncedOldTelemetry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteSyncedOldTelemetry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllTelemetry(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTelemetry.acquire();
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
          __preparedStmtOfDeleteAllTelemetry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TelemetryEntity>> getRecentTelemetry(final int limit) {
    final String _sql = "SELECT * FROM telemetry ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Flow<List<TelemetryEntity>> getTelemetryByDevice(final String deviceId, final int limit) {
    final String _sql = "SELECT * FROM telemetry WHERE deviceId = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Flow<List<TelemetryEntity>> getTelemetryByDeviceAndSensor(final String deviceId,
      final String sensorType, final int limit) {
    final String _sql = "SELECT * FROM telemetry WHERE deviceId = ? AND sensorType = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Flow<List<TelemetryEntity>> getTelemetryByTimeRange(final String deviceId,
      final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM telemetry WHERE deviceId = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Flow<List<TelemetryEntity>> getTelemetryBySensorType(final String sensorType,
      final int limit) {
    final String _sql = "SELECT * FROM telemetry WHERE sensorType = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Object getLatestTelemetryForDevice(final String deviceId,
      final Continuation<? super TelemetryEntity> $completion) {
    final String _sql = "SELECT * FROM telemetry WHERE deviceId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TelemetryEntity>() {
      @Override
      @Nullable
      public TelemetryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TelemetryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Object getLatestTelemetryForSensor(final String deviceId, final String sensorType,
      final Continuation<? super TelemetryEntity> $completion) {
    final String _sql = "SELECT * FROM telemetry WHERE deviceId = ? AND sensorType = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TelemetryEntity>() {
      @Override
      @Nullable
      public TelemetryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TelemetryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Object getAverageTelemetryValue(final String deviceId, final String sensorType,
      final long startTime, final long endTime, final Continuation<? super Double> $completion) {
    final String _sql = "SELECT AVG(value) FROM telemetry WHERE deviceId = ? AND sensorType = ? AND timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 3;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endTime);
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
  public Object getMinTelemetryValue(final String deviceId, final String sensorType,
      final long startTime, final long endTime, final Continuation<? super Double> $completion) {
    final String _sql = "SELECT MIN(value) FROM telemetry WHERE deviceId = ? AND sensorType = ? AND timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 3;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endTime);
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
  public Object getMaxTelemetryValue(final String deviceId, final String sensorType,
      final long startTime, final long endTime, final Continuation<? super Double> $completion) {
    final String _sql = "SELECT MAX(value) FROM telemetry WHERE deviceId = ? AND sensorType = ? AND timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 3;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endTime);
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
  public Object getTelemetryCountForDevice(final String deviceId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM telemetry WHERE deviceId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
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

  @Override
  public Object getTelemetryCountSince(final String deviceId, final long timestamp,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM telemetry WHERE deviceId = ? AND timestamp > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, timestamp);
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

  @Override
  public Object getSensorTypesForDevice(final String deviceId,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT sensorType FROM telemetry WHERE deviceId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
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
  public Object getAllSensorTypes(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT sensorType FROM telemetry";
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
  public Object getTelemetryBySyncStatus(final String status,
      final Continuation<? super List<TelemetryEntity>> $completion) {
    final String _sql = "SELECT * FROM telemetry WHERE syncStatus = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Object getPendingSyncTelemetry(final int limit,
      final Continuation<? super List<TelemetryEntity>> $completion) {
    final String _sql = "SELECT * FROM telemetry WHERE syncStatus = 'PENDING' ORDER BY timestamp ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Flow<List<TelemetryEntity>> getTelemetryByQuality(final String quality, final int limit) {
    final String _sql = "SELECT * FROM telemetry WHERE quality = ? ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, quality);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry"}, new Callable<List<TelemetryEntity>>() {
      @Override
      @NonNull
      public List<TelemetryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfSensorType = CursorUtil.getColumnIndexOrThrow(_cursor, "sensorType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfRawValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rawValue");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TelemetryEntity> _result = new ArrayList<TelemetryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpValue;
            _tmpValue = _cursor.getDouble(_cursorIndexOfValue);
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpRawValue;
            if (_cursor.isNull(_cursorIndexOfRawValue)) {
              _tmpRawValue = null;
            } else {
              _tmpRawValue = _cursor.getString(_cursorIndexOfRawValue);
            }
            final String _tmpMetadata;
            if (_cursor.isNull(_cursorIndexOfMetadata)) {
              _tmpMetadata = null;
            } else {
              _tmpMetadata = _cursor.getString(_cursorIndexOfMetadata);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TelemetryEntity(_tmpId,_tmpDeviceId,_tmpSensorType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpQuality,_tmpRawValue,_tmpMetadata,_tmpIsProcessed,_tmpSyncStatus,_tmpCreatedAt);
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
  public Object getTelemetryStatsByDevice(final String deviceId, final long startTime,
      final long endTime, final Continuation<? super List<TelemetryStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT sensorType, AVG(value) as avgValue, MIN(value) as minValue, MAX(value) as maxValue, COUNT(*) as count\n"
            + "        FROM telemetry \n"
            + "        WHERE deviceId = ? AND timestamp BETWEEN ? AND ? \n"
            + "        GROUP BY sensorType\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TelemetryStats>>() {
      @Override
      @NonNull
      public List<TelemetryStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSensorType = 0;
          final int _cursorIndexOfAvgValue = 1;
          final int _cursorIndexOfMinValue = 2;
          final int _cursorIndexOfMaxValue = 3;
          final int _cursorIndexOfCount = 4;
          final List<TelemetryStats> _result = new ArrayList<TelemetryStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryStats _item;
            final String _tmpSensorType;
            _tmpSensorType = _cursor.getString(_cursorIndexOfSensorType);
            final double _tmpAvgValue;
            _tmpAvgValue = _cursor.getDouble(_cursorIndexOfAvgValue);
            final double _tmpMinValue;
            _tmpMinValue = _cursor.getDouble(_cursorIndexOfMinValue);
            final double _tmpMaxValue;
            _tmpMaxValue = _cursor.getDouble(_cursorIndexOfMaxValue);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new TelemetryStats(_tmpSensorType,_tmpAvgValue,_tmpMinValue,_tmpMaxValue,_tmpCount);
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
  public Object getHourlyTelemetryStats(final String deviceId, final String sensorType,
      final long startTime, final long endTime,
      final Continuation<? super List<HourlyTelemetryStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            strftime('%Y-%m-%d %H:00:00', datetime(timestamp/1000, 'unixepoch')) as hour,\n"
            + "            AVG(value) as avgValue,\n"
            + "            MIN(value) as minValue,\n"
            + "            MAX(value) as maxValue,\n"
            + "            COUNT(*) as count\n"
            + "        FROM telemetry \n"
            + "        WHERE deviceId = ? AND sensorType = ? AND timestamp BETWEEN ? AND ? \n"
            + "        GROUP BY hour\n"
            + "        ORDER BY hour\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, sensorType);
    _argIndex = 3;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HourlyTelemetryStats>>() {
      @Override
      @NonNull
      public List<HourlyTelemetryStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHour = 0;
          final int _cursorIndexOfAvgValue = 1;
          final int _cursorIndexOfMinValue = 2;
          final int _cursorIndexOfMaxValue = 3;
          final int _cursorIndexOfCount = 4;
          final List<HourlyTelemetryStats> _result = new ArrayList<HourlyTelemetryStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HourlyTelemetryStats _item;
            final String _tmpHour;
            _tmpHour = _cursor.getString(_cursorIndexOfHour);
            final double _tmpAvgValue;
            _tmpAvgValue = _cursor.getDouble(_cursorIndexOfAvgValue);
            final double _tmpMinValue;
            _tmpMinValue = _cursor.getDouble(_cursorIndexOfMinValue);
            final double _tmpMaxValue;
            _tmpMaxValue = _cursor.getDouble(_cursorIndexOfMaxValue);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new HourlyTelemetryStats(_tmpHour,_tmpAvgValue,_tmpMinValue,_tmpMaxValue,_tmpCount);
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
  public Object updateSyncStatusBatch(final List<String> telemetryIds, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE telemetry SET syncStatus = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = telemetryIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        for (String _item : telemetryIds) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

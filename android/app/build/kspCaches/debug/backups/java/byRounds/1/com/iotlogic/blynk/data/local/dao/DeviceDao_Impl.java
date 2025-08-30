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
import androidx.sqlite.db.SupportSQLiteStatement;
import com.iotlogic.blynk.data.local.entities.DeviceConverters;
import com.iotlogic.blynk.data.local.entities.DeviceEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeviceEntity> __insertionAdapterOfDeviceEntity;

  private final DeviceConverters __deviceConverters = new DeviceConverters();

  private final EntityDeletionOrUpdateAdapter<DeviceEntity> __deletionAdapterOfDeviceEntity;

  private final EntityDeletionOrUpdateAdapter<DeviceEntity> __updateAdapterOfDeviceEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDeviceStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDeviceOnlineStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBatteryLevel;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSignalStrength;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDeviceLocation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDeviceById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDevicesByUser;

  private final SharedSQLiteStatement __preparedStmtOfDeleteInactiveDevices;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllDevices;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeviceEntity = new EntityInsertionAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `devices` (`id`,`name`,`type`,`protocol`,`status`,`token`,`userId`,`macAddress`,`ipAddress`,`port`,`lastSeen`,`batteryLevel`,`signalStrength`,`firmwareVersion`,`hardwareVersion`,`manufacturer`,`modelNumber`,`serialNumber`,`location`,`latitude`,`longitude`,`configuration`,`metadata`,`isOnline`,`connectionQuality`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getProtocol());
        statement.bindString(5, entity.getStatus());
        statement.bindString(6, entity.getToken());
        statement.bindString(7, entity.getUserId());
        if (entity.getMacAddress() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMacAddress());
        }
        if (entity.getIpAddress() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getIpAddress());
        }
        if (entity.getPort() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getPort());
        }
        statement.bindLong(11, entity.getLastSeen());
        if (entity.getBatteryLevel() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getBatteryLevel());
        }
        if (entity.getSignalStrength() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getSignalStrength());
        }
        if (entity.getFirmwareVersion() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getFirmwareVersion());
        }
        if (entity.getHardwareVersion() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getHardwareVersion());
        }
        if (entity.getManufacturer() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getManufacturer());
        }
        if (entity.getModelNumber() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getModelNumber());
        }
        if (entity.getSerialNumber() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getSerialNumber());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getLocation());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(20);
        } else {
          statement.bindDouble(20, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getLongitude());
        }
        final String _tmp = __deviceConverters.fromStringMap(entity.getConfiguration());
        statement.bindString(22, _tmp);
        final String _tmp_1 = __deviceConverters.fromStringMap(entity.getMetadata());
        statement.bindString(23, _tmp_1);
        final int _tmp_2 = entity.isOnline() ? 1 : 0;
        statement.bindLong(24, _tmp_2);
        if (entity.getConnectionQuality() == null) {
          statement.bindNull(25);
        } else {
          statement.bindString(25, entity.getConnectionQuality());
        }
        statement.bindLong(26, entity.getCreatedAt());
        statement.bindLong(27, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfDeviceEntity = new EntityDeletionOrUpdateAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `devices` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfDeviceEntity = new EntityDeletionOrUpdateAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `devices` SET `id` = ?,`name` = ?,`type` = ?,`protocol` = ?,`status` = ?,`token` = ?,`userId` = ?,`macAddress` = ?,`ipAddress` = ?,`port` = ?,`lastSeen` = ?,`batteryLevel` = ?,`signalStrength` = ?,`firmwareVersion` = ?,`hardwareVersion` = ?,`manufacturer` = ?,`modelNumber` = ?,`serialNumber` = ?,`location` = ?,`latitude` = ?,`longitude` = ?,`configuration` = ?,`metadata` = ?,`isOnline` = ?,`connectionQuality` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getProtocol());
        statement.bindString(5, entity.getStatus());
        statement.bindString(6, entity.getToken());
        statement.bindString(7, entity.getUserId());
        if (entity.getMacAddress() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMacAddress());
        }
        if (entity.getIpAddress() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getIpAddress());
        }
        if (entity.getPort() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getPort());
        }
        statement.bindLong(11, entity.getLastSeen());
        if (entity.getBatteryLevel() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getBatteryLevel());
        }
        if (entity.getSignalStrength() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getSignalStrength());
        }
        if (entity.getFirmwareVersion() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getFirmwareVersion());
        }
        if (entity.getHardwareVersion() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getHardwareVersion());
        }
        if (entity.getManufacturer() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getManufacturer());
        }
        if (entity.getModelNumber() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getModelNumber());
        }
        if (entity.getSerialNumber() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getSerialNumber());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getLocation());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(20);
        } else {
          statement.bindDouble(20, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getLongitude());
        }
        final String _tmp = __deviceConverters.fromStringMap(entity.getConfiguration());
        statement.bindString(22, _tmp);
        final String _tmp_1 = __deviceConverters.fromStringMap(entity.getMetadata());
        statement.bindString(23, _tmp_1);
        final int _tmp_2 = entity.isOnline() ? 1 : 0;
        statement.bindLong(24, _tmp_2);
        if (entity.getConnectionQuality() == null) {
          statement.bindNull(25);
        } else {
          statement.bindString(25, entity.getConnectionQuality());
        }
        statement.bindLong(26, entity.getCreatedAt());
        statement.bindLong(27, entity.getUpdatedAt());
        statement.bindString(28, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateDeviceStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE devices SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDeviceOnlineStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE devices SET isOnline = ?, lastSeen = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBatteryLevel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE devices SET batteryLevel = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSignalStrength = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE devices SET signalStrength = ?, connectionQuality = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDeviceLocation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE devices SET latitude = ?, longitude = ?, location = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDeviceById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM devices WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDevicesByUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM devices WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteInactiveDevices = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM devices WHERE lastSeen < ? AND isOnline = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllDevices = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM devices";
        return _query;
      }
    };
  }

  @Override
  public Object insertDevice(final DeviceEntity device,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDeviceEntity.insert(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDevices(final List<DeviceEntity> devices,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDeviceEntity.insert(devices);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDevice(final DeviceEntity device,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDeviceEntity.handle(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDevice(final DeviceEntity device,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDeviceEntity.handle(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDeviceStatus(final String deviceId, final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDeviceStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
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
          __preparedStmtOfUpdateDeviceStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDeviceOnlineStatus(final String deviceId, final boolean isOnline,
      final long lastSeen, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDeviceOnlineStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isOnline ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, lastSeen);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
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
          __preparedStmtOfUpdateDeviceOnlineStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBatteryLevel(final String deviceId, final int batteryLevel,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBatteryLevel.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, batteryLevel);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
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
          __preparedStmtOfUpdateBatteryLevel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSignalStrength(final String deviceId, final int signalStrength,
      final String quality, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSignalStrength.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, signalStrength);
        _argIndex = 2;
        _stmt.bindString(_argIndex, quality);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
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
          __preparedStmtOfUpdateSignalStrength.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDeviceLocation(final String deviceId, final Double latitude,
      final Double longitude, final String location, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDeviceLocation.acquire();
        int _argIndex = 1;
        if (latitude == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, latitude);
        }
        _argIndex = 2;
        if (longitude == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindDouble(_argIndex, longitude);
        }
        _argIndex = 3;
        if (location == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, location);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 5;
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
          __preparedStmtOfUpdateDeviceLocation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDeviceById(final String deviceId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDeviceById.acquire();
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
          __preparedStmtOfDeleteDeviceById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDevicesByUser(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDevicesByUser.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfDeleteDevicesByUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInactiveDevices(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteInactiveDevices.acquire();
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
          __preparedStmtOfDeleteInactiveDevices.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllDevices(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllDevices.acquire();
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
          __preparedStmtOfDeleteAllDevices.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DeviceEntity>> getAllDevices() {
    final String _sql = "SELECT * FROM devices ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDeviceById(final String deviceId,
      final Continuation<? super DeviceEntity> $completion) {
    final String _sql = "SELECT * FROM devices WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DeviceEntity>() {
      @Override
      @Nullable
      public DeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<DeviceEntity> observeDeviceById(final String deviceId) {
    final String _sql = "SELECT * FROM devices WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<DeviceEntity>() {
      @Override
      @Nullable
      public DeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDeviceByToken(final String token,
      final Continuation<? super DeviceEntity> $completion) {
    final String _sql = "SELECT * FROM devices WHERE token = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, token);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DeviceEntity>() {
      @Override
      @Nullable
      public DeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getDevicesByUser(final String userId) {
    final String _sql = "SELECT * FROM devices WHERE userId = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getDevicesByProtocol(final String protocol) {
    final String _sql = "SELECT * FROM devices WHERE protocol = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, protocol);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getDevicesByStatus(final String status) {
    final String _sql = "SELECT * FROM devices WHERE status = ? ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getOnlineDevices() {
    final String _sql = "SELECT * FROM devices WHERE isOnline = 1 ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getOfflineDevices() {
    final String _sql = "SELECT * FROM devices WHERE isOnline = 0 ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getRecentlyActiveDevices(final long timestamp) {
    final String _sql = "SELECT * FROM devices WHERE lastSeen > ? ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, timestamp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> searchDevices(final String query) {
    final String _sql = "SELECT * FROM devices WHERE name LIKE '%' || ? || '%' OR type LIKE '%' || ? || '%' ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<DeviceEntity>> getDevicesWithLocation() {
    final String _sql = "SELECT * FROM devices WHERE latitude IS NOT NULL AND longitude IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final int _cursorIndexOfIpAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "ipAddress");
          final int _cursorIndexOfPort = CursorUtil.getColumnIndexOrThrow(_cursor, "port");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signalStrength");
          final int _cursorIndexOfFirmwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "firmwareVersion");
          final int _cursorIndexOfHardwareVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "hardwareVersion");
          final int _cursorIndexOfManufacturer = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer");
          final int _cursorIndexOfModelNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "modelNumber");
          final int _cursorIndexOfSerialNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "serialNumber");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfConfiguration = CursorUtil.getColumnIndexOrThrow(_cursor, "configuration");
          final int _cursorIndexOfMetadata = CursorUtil.getColumnIndexOrThrow(_cursor, "metadata");
          final int _cursorIndexOfIsOnline = CursorUtil.getColumnIndexOrThrow(_cursor, "isOnline");
          final int _cursorIndexOfConnectionQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "connectionQuality");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            final String _tmpIpAddress;
            if (_cursor.isNull(_cursorIndexOfIpAddress)) {
              _tmpIpAddress = null;
            } else {
              _tmpIpAddress = _cursor.getString(_cursorIndexOfIpAddress);
            }
            final Integer _tmpPort;
            if (_cursor.isNull(_cursorIndexOfPort)) {
              _tmpPort = null;
            } else {
              _tmpPort = _cursor.getInt(_cursorIndexOfPort);
            }
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final Integer _tmpSignalStrength;
            if (_cursor.isNull(_cursorIndexOfSignalStrength)) {
              _tmpSignalStrength = null;
            } else {
              _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            }
            final String _tmpFirmwareVersion;
            if (_cursor.isNull(_cursorIndexOfFirmwareVersion)) {
              _tmpFirmwareVersion = null;
            } else {
              _tmpFirmwareVersion = _cursor.getString(_cursorIndexOfFirmwareVersion);
            }
            final String _tmpHardwareVersion;
            if (_cursor.isNull(_cursorIndexOfHardwareVersion)) {
              _tmpHardwareVersion = null;
            } else {
              _tmpHardwareVersion = _cursor.getString(_cursorIndexOfHardwareVersion);
            }
            final String _tmpManufacturer;
            if (_cursor.isNull(_cursorIndexOfManufacturer)) {
              _tmpManufacturer = null;
            } else {
              _tmpManufacturer = _cursor.getString(_cursorIndexOfManufacturer);
            }
            final String _tmpModelNumber;
            if (_cursor.isNull(_cursorIndexOfModelNumber)) {
              _tmpModelNumber = null;
            } else {
              _tmpModelNumber = _cursor.getString(_cursorIndexOfModelNumber);
            }
            final String _tmpSerialNumber;
            if (_cursor.isNull(_cursorIndexOfSerialNumber)) {
              _tmpSerialNumber = null;
            } else {
              _tmpSerialNumber = _cursor.getString(_cursorIndexOfSerialNumber);
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Map<String, String> _tmpConfiguration;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfConfiguration);
            _tmpConfiguration = __deviceConverters.toStringMap(_tmp);
            final Map<String, String> _tmpMetadata;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMetadata);
            _tmpMetadata = __deviceConverters.toStringMap(_tmp_1);
            final boolean _tmpIsOnline;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsOnline);
            _tmpIsOnline = _tmp_2 != 0;
            final String _tmpConnectionQuality;
            if (_cursor.isNull(_cursorIndexOfConnectionQuality)) {
              _tmpConnectionQuality = null;
            } else {
              _tmpConnectionQuality = _cursor.getString(_cursorIndexOfConnectionQuality);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DeviceEntity(_tmpId,_tmpName,_tmpType,_tmpProtocol,_tmpStatus,_tmpToken,_tmpUserId,_tmpMacAddress,_tmpIpAddress,_tmpPort,_tmpLastSeen,_tmpBatteryLevel,_tmpSignalStrength,_tmpFirmwareVersion,_tmpHardwareVersion,_tmpManufacturer,_tmpModelNumber,_tmpSerialNumber,_tmpLocation,_tmpLatitude,_tmpLongitude,_tmpConfiguration,_tmpMetadata,_tmpIsOnline,_tmpConnectionQuality,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getDeviceCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM devices";
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

  @Override
  public Object getDeviceCountByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM devices WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
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
  public Object getDeviceCountByProtocol(final String protocol,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM devices WHERE protocol = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, protocol);
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
  public Object getUsedProtocols(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT protocol FROM devices";
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
  public Object getDeviceTypes(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT type FROM devices";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

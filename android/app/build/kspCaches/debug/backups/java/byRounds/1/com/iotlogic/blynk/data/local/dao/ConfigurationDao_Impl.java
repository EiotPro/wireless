package com.iotlogic.blynk.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.iotlogic.blynk.data.local.entities.ConfigurationConverters;
import com.iotlogic.blynk.data.local.entities.ConfigurationEntity;
import java.lang.Class;
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
import java.util.Map;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ConfigurationDao_Impl implements ConfigurationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConfigurationEntity> __insertionAdapterOfConfigurationEntity;

  private final ConfigurationConverters __configurationConverters = new ConfigurationConverters();

  private final EntityDeletionOrUpdateAdapter<ConfigurationEntity> __deletionAdapterOfConfigurationEntity;

  private final EntityDeletionOrUpdateAdapter<ConfigurationEntity> __updateAdapterOfConfigurationEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateConfigurationValue;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConfiguration;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConfigurationById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConfigurationsByDevice;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConfigurationsByCategory;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllConfigurations;

  public ConfigurationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConfigurationEntity = new EntityInsertionAdapter<ConfigurationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `configurations` (`id`,`deviceId`,`configKey`,`configValue`,`dataType`,`category`,`description`,`isReadOnly`,`validationRules`,`defaultValue`,`unit`,`priority`,`syncStatus`,`lastModified`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConfigurationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDeviceId());
        statement.bindString(3, entity.getConfigKey());
        statement.bindString(4, entity.getConfigValue());
        statement.bindString(5, entity.getDataType());
        statement.bindString(6, entity.getCategory());
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        final int _tmp = entity.isReadOnly() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final String _tmp_1 = __configurationConverters.fromValidationRules(entity.getValidationRules());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        if (entity.getDefaultValue() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getDefaultValue());
        }
        if (entity.getUnit() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getUnit());
        }
        statement.bindLong(12, entity.getPriority());
        statement.bindString(13, entity.getSyncStatus());
        statement.bindLong(14, entity.getLastModified());
        statement.bindLong(15, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfConfigurationEntity = new EntityDeletionOrUpdateAdapter<ConfigurationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `configurations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConfigurationEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfConfigurationEntity = new EntityDeletionOrUpdateAdapter<ConfigurationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `configurations` SET `id` = ?,`deviceId` = ?,`configKey` = ?,`configValue` = ?,`dataType` = ?,`category` = ?,`description` = ?,`isReadOnly` = ?,`validationRules` = ?,`defaultValue` = ?,`unit` = ?,`priority` = ?,`syncStatus` = ?,`lastModified` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConfigurationEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDeviceId());
        statement.bindString(3, entity.getConfigKey());
        statement.bindString(4, entity.getConfigValue());
        statement.bindString(5, entity.getDataType());
        statement.bindString(6, entity.getCategory());
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        final int _tmp = entity.isReadOnly() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final String _tmp_1 = __configurationConverters.fromValidationRules(entity.getValidationRules());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_1);
        }
        if (entity.getDefaultValue() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getDefaultValue());
        }
        if (entity.getUnit() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getUnit());
        }
        statement.bindLong(12, entity.getPriority());
        statement.bindString(13, entity.getSyncStatus());
        statement.bindLong(14, entity.getLastModified());
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindString(16, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateConfigurationValue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE configurations SET configValue = ?, lastModified = ?, syncStatus = 'PENDING' WHERE deviceId = ? AND configKey = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE configurations SET syncStatus = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConfiguration = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM configurations WHERE deviceId = ? AND configKey = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConfigurationById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM configurations WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConfigurationsByDevice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM configurations WHERE deviceId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConfigurationsByCategory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM configurations WHERE deviceId = ? AND category = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllConfigurations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM configurations";
        return _query;
      }
    };
  }

  @Override
  public Object insertConfiguration(final ConfigurationEntity configuration,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfConfigurationEntity.insert(configuration);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertConfigurations(final List<ConfigurationEntity> configurations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfConfigurationEntity.insert(configurations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConfiguration(final ConfigurationEntity configuration,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfConfigurationEntity.handle(configuration);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateConfiguration(final ConfigurationEntity configuration,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfConfigurationEntity.handle(configuration);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object resetDeviceConfigurations(final String deviceId,
      final List<ConfigurationEntity> defaultConfigurations,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ConfigurationDao.super.resetDeviceConfigurations(deviceId, defaultConfigurations, __cont), $completion);
  }

  @Override
  public Object updateDeviceConfigurations(final String deviceId,
      final Map<String, String> configurations, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ConfigurationDao.super.updateDeviceConfigurations(deviceId, configurations, __cont), $completion);
  }

  @Override
  public Object importDeviceConfigurations(final String deviceId,
      final List<ConfigurationEntity> configurations,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> ConfigurationDao.super.importDeviceConfigurations(deviceId, configurations, __cont), $completion);
  }

  @Override
  public Object updateConfigurationValue(final String deviceId, final String configKey,
      final String value, final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateConfigurationValue.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, value);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindString(_argIndex, deviceId);
        _argIndex = 4;
        _stmt.bindString(_argIndex, configKey);
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
          __preparedStmtOfUpdateConfigurationValue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String configurationId, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, configurationId);
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
  public Object deleteConfiguration(final String deviceId, final String configKey,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConfiguration.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, deviceId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, configKey);
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
          __preparedStmtOfDeleteConfiguration.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConfigurationById(final String configurationId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConfigurationById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, configurationId);
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
          __preparedStmtOfDeleteConfigurationById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConfigurationsByDevice(final String deviceId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConfigurationsByDevice.acquire();
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
          __preparedStmtOfDeleteConfigurationsByDevice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteConfigurationsByCategory(final String deviceId, final String category,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConfigurationsByCategory.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, deviceId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, category);
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
          __preparedStmtOfDeleteConfigurationsByCategory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllConfigurations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllConfigurations.acquire();
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
          __preparedStmtOfDeleteAllConfigurations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ConfigurationEntity>> getConfigurationsByDevice(final String deviceId) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ? ORDER BY priority DESC, configKey ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Flow<List<ConfigurationEntity>> getConfigurationsByDeviceAndCategory(final String deviceId,
      final String category) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ? AND category = ? ORDER BY priority DESC, configKey ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getConfiguration(final String deviceId, final String configKey,
      final Continuation<? super ConfigurationEntity> $completion) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ? AND configKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, configKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ConfigurationEntity>() {
      @Override
      @Nullable
      public ConfigurationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ConfigurationEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Flow<ConfigurationEntity> observeConfiguration(final String deviceId,
      final String configKey) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ? AND configKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, configKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<ConfigurationEntity>() {
      @Override
      @Nullable
      public ConfigurationEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ConfigurationEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getConfigurationValue(final String deviceId, final String configKey,
      final Continuation<? super String> $completion) {
    final String _sql = "SELECT configValue FROM configurations WHERE deviceId = ? AND configKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, configKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
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
  public Flow<List<ConfigurationEntity>> getConfigurationsByCategory(final String category) {
    final String _sql = "SELECT * FROM configurations WHERE category = ? ORDER BY deviceId, priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getCategoriesForDevice(final String deviceId,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT category FROM configurations WHERE deviceId = ?";
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
  public Object getAllCategories(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT category FROM configurations";
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
  public Object getConfigKeysForCategory(final String category,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT configKey FROM configurations WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
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
  public Flow<List<ConfigurationEntity>> getEditableConfigurations(final String deviceId) {
    final String _sql = "SELECT * FROM configurations WHERE isReadOnly = 0 AND deviceId = ? ORDER BY priority DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Flow<List<ConfigurationEntity>> getReadOnlyConfigurations(final String deviceId) {
    final String _sql = "SELECT * FROM configurations WHERE isReadOnly = 1 AND deviceId = ? ORDER BY configKey ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"configurations"}, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getConfigurationsBySyncStatus(final String status,
      final Continuation<? super List<ConfigurationEntity>> $completion) {
    final String _sql = "SELECT * FROM configurations WHERE syncStatus = ? ORDER BY lastModified ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getPendingSyncConfigurations(final int limit,
      final Continuation<? super List<ConfigurationEntity>> $completion) {
    final String _sql = "SELECT * FROM configurations WHERE syncStatus = 'PENDING' ORDER BY priority DESC, lastModified ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object getConfigurationCountForDevice(final String deviceId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM configurations WHERE deviceId = ?";
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
  public Object getConfigurationCountForCategory(final String deviceId, final String category,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM configurations WHERE deviceId = ? AND category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    _argIndex = 2;
    _statement.bindString(_argIndex, category);
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
  public Object getConfigurationsWithValidation(final String deviceId,
      final Continuation<? super List<ConfigurationEntity>> $completion) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ? AND validationRules IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object exportDeviceConfigurations(final String deviceId,
      final Continuation<? super List<ConfigurationEntity>> $completion) {
    final String _sql = "SELECT * FROM configurations WHERE deviceId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ConfigurationEntity>>() {
      @Override
      @NonNull
      public List<ConfigurationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfConfigKey = CursorUtil.getColumnIndexOrThrow(_cursor, "configKey");
          final int _cursorIndexOfConfigValue = CursorUtil.getColumnIndexOrThrow(_cursor, "configValue");
          final int _cursorIndexOfDataType = CursorUtil.getColumnIndexOrThrow(_cursor, "dataType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsReadOnly = CursorUtil.getColumnIndexOrThrow(_cursor, "isReadOnly");
          final int _cursorIndexOfValidationRules = CursorUtil.getColumnIndexOrThrow(_cursor, "validationRules");
          final int _cursorIndexOfDefaultValue = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultValue");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfLastModified = CursorUtil.getColumnIndexOrThrow(_cursor, "lastModified");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ConfigurationEntity> _result = new ArrayList<ConfigurationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConfigurationEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpConfigKey;
            _tmpConfigKey = _cursor.getString(_cursorIndexOfConfigKey);
            final String _tmpConfigValue;
            _tmpConfigValue = _cursor.getString(_cursorIndexOfConfigValue);
            final String _tmpDataType;
            _tmpDataType = _cursor.getString(_cursorIndexOfDataType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final boolean _tmpIsReadOnly;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReadOnly);
            _tmpIsReadOnly = _tmp != 0;
            final Map<String, Object> _tmpValidationRules;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfValidationRules)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfValidationRules);
            }
            _tmpValidationRules = __configurationConverters.toValidationRules(_tmp_1);
            final String _tmpDefaultValue;
            if (_cursor.isNull(_cursorIndexOfDefaultValue)) {
              _tmpDefaultValue = null;
            } else {
              _tmpDefaultValue = _cursor.getString(_cursorIndexOfDefaultValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final long _tmpLastModified;
            _tmpLastModified = _cursor.getLong(_cursorIndexOfLastModified);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ConfigurationEntity(_tmpId,_tmpDeviceId,_tmpConfigKey,_tmpConfigValue,_tmpDataType,_tmpCategory,_tmpDescription,_tmpIsReadOnly,_tmpValidationRules,_tmpDefaultValue,_tmpUnit,_tmpPriority,_tmpSyncStatus,_tmpLastModified,_tmpCreatedAt);
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
  public Object updateSyncStatusBatch(final List<String> configurationIds, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE configurations SET syncStatus = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = configurationIds.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        for (String _item : configurationIds) {
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

  @Override
  public Object markConfigurationsForSync(final String deviceId, final List<String> configKeys,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE configurations SET syncStatus = 'PENDING', lastModified = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE deviceId = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" AND configKey IN (");
        final int _inputSize = configKeys.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, deviceId);
        _argIndex = 3;
        for (String _item : configKeys) {
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

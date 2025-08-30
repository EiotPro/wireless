package com.iotlogic.blynk.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.iotlogic.blynk.data.local.dao.CommandQueueDao;
import com.iotlogic.blynk.data.local.dao.CommandQueueDao_Impl;
import com.iotlogic.blynk.data.local.dao.ConfigurationDao;
import com.iotlogic.blynk.data.local.dao.ConfigurationDao_Impl;
import com.iotlogic.blynk.data.local.dao.DeviceDao;
import com.iotlogic.blynk.data.local.dao.DeviceDao_Impl;
import com.iotlogic.blynk.data.local.dao.TelemetryDao;
import com.iotlogic.blynk.data.local.dao.TelemetryDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IoTLogicDatabase_Impl extends IoTLogicDatabase {
  private volatile DeviceDao _deviceDao;

  private volatile TelemetryDao _telemetryDao;

  private volatile ConfigurationDao _configurationDao;

  private volatile CommandQueueDao _commandQueueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `devices` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `protocol` TEXT NOT NULL, `status` TEXT NOT NULL, `token` TEXT NOT NULL, `userId` TEXT NOT NULL, `macAddress` TEXT, `ipAddress` TEXT, `port` INTEGER, `lastSeen` INTEGER NOT NULL, `batteryLevel` INTEGER, `signalStrength` INTEGER, `firmwareVersion` TEXT, `hardwareVersion` TEXT, `manufacturer` TEXT, `modelNumber` TEXT, `serialNumber` TEXT, `location` TEXT, `latitude` REAL, `longitude` REAL, `configuration` TEXT NOT NULL, `metadata` TEXT NOT NULL, `isOnline` INTEGER NOT NULL, `connectionQuality` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `telemetry` (`id` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `sensorType` TEXT NOT NULL, `value` REAL NOT NULL, `unit` TEXT, `timestamp` INTEGER NOT NULL, `quality` TEXT, `rawValue` TEXT, `metadata` TEXT, `isProcessed` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`deviceId`) REFERENCES `devices`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_telemetry_deviceId` ON `telemetry` (`deviceId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_telemetry_timestamp` ON `telemetry` (`timestamp`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_telemetry_sensorType` ON `telemetry` (`sensorType`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `configurations` (`id` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `configKey` TEXT NOT NULL, `configValue` TEXT NOT NULL, `dataType` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT, `isReadOnly` INTEGER NOT NULL, `validationRules` TEXT, `defaultValue` TEXT, `unit` TEXT, `priority` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, `lastModified` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`deviceId`) REFERENCES `devices`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_configurations_deviceId` ON `configurations` (`deviceId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_configurations_configKey` ON `configurations` (`configKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `command_queue` (`id` TEXT NOT NULL, `deviceId` TEXT NOT NULL, `command` TEXT NOT NULL, `parameters` TEXT NOT NULL, `priority` INTEGER NOT NULL, `status` TEXT NOT NULL, `retryCount` INTEGER NOT NULL, `maxRetries` INTEGER NOT NULL, `result` TEXT, `errorMessage` TEXT, `createdAt` INTEGER NOT NULL, `scheduledAt` INTEGER, `sentAt` INTEGER, `completedAt` INTEGER, `expiresAt` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`deviceId`) REFERENCES `devices`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_deviceId` ON `command_queue` (`deviceId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_status` ON `command_queue` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_priority` ON `command_queue` (`priority`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_command_queue_createdAt` ON `command_queue` (`createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ad5c5e191c52a37f6895759b2e5025a7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `devices`");
        db.execSQL("DROP TABLE IF EXISTS `telemetry`");
        db.execSQL("DROP TABLE IF EXISTS `configurations`");
        db.execSQL("DROP TABLE IF EXISTS `command_queue`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDevices = new HashMap<String, TableInfo.Column>(27);
        _columnsDevices.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("token", new TableInfo.Column("token", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("macAddress", new TableInfo.Column("macAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("ipAddress", new TableInfo.Column("ipAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("port", new TableInfo.Column("port", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("lastSeen", new TableInfo.Column("lastSeen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("batteryLevel", new TableInfo.Column("batteryLevel", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("signalStrength", new TableInfo.Column("signalStrength", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("firmwareVersion", new TableInfo.Column("firmwareVersion", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("hardwareVersion", new TableInfo.Column("hardwareVersion", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("manufacturer", new TableInfo.Column("manufacturer", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("modelNumber", new TableInfo.Column("modelNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("serialNumber", new TableInfo.Column("serialNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("location", new TableInfo.Column("location", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("configuration", new TableInfo.Column("configuration", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("metadata", new TableInfo.Column("metadata", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("isOnline", new TableInfo.Column("isOnline", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("connectionQuality", new TableInfo.Column("connectionQuality", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDevices.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDevices = new TableInfo("devices", _columnsDevices, _foreignKeysDevices, _indicesDevices);
        final TableInfo _existingDevices = TableInfo.read(db, "devices");
        if (!_infoDevices.equals(_existingDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "devices(com.iotlogic.blynk.data.local.entities.DeviceEntity).\n"
                  + " Expected:\n" + _infoDevices + "\n"
                  + " Found:\n" + _existingDevices);
        }
        final HashMap<String, TableInfo.Column> _columnsTelemetry = new HashMap<String, TableInfo.Column>(12);
        _columnsTelemetry.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("sensorType", new TableInfo.Column("sensorType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("value", new TableInfo.Column("value", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("unit", new TableInfo.Column("unit", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("quality", new TableInfo.Column("quality", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("rawValue", new TableInfo.Column("rawValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("metadata", new TableInfo.Column("metadata", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("isProcessed", new TableInfo.Column("isProcessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetry.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTelemetry = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysTelemetry.add(new TableInfo.ForeignKey("devices", "CASCADE", "NO ACTION", Arrays.asList("deviceId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTelemetry = new HashSet<TableInfo.Index>(3);
        _indicesTelemetry.add(new TableInfo.Index("index_telemetry_deviceId", false, Arrays.asList("deviceId"), Arrays.asList("ASC")));
        _indicesTelemetry.add(new TableInfo.Index("index_telemetry_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        _indicesTelemetry.add(new TableInfo.Index("index_telemetry_sensorType", false, Arrays.asList("sensorType"), Arrays.asList("ASC")));
        final TableInfo _infoTelemetry = new TableInfo("telemetry", _columnsTelemetry, _foreignKeysTelemetry, _indicesTelemetry);
        final TableInfo _existingTelemetry = TableInfo.read(db, "telemetry");
        if (!_infoTelemetry.equals(_existingTelemetry)) {
          return new RoomOpenHelper.ValidationResult(false, "telemetry(com.iotlogic.blynk.data.local.entities.TelemetryEntity).\n"
                  + " Expected:\n" + _infoTelemetry + "\n"
                  + " Found:\n" + _existingTelemetry);
        }
        final HashMap<String, TableInfo.Column> _columnsConfigurations = new HashMap<String, TableInfo.Column>(15);
        _columnsConfigurations.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("configKey", new TableInfo.Column("configKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("configValue", new TableInfo.Column("configValue", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("dataType", new TableInfo.Column("dataType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("isReadOnly", new TableInfo.Column("isReadOnly", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("validationRules", new TableInfo.Column("validationRules", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("defaultValue", new TableInfo.Column("defaultValue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("unit", new TableInfo.Column("unit", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("lastModified", new TableInfo.Column("lastModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConfigurations.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysConfigurations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysConfigurations.add(new TableInfo.ForeignKey("devices", "CASCADE", "NO ACTION", Arrays.asList("deviceId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesConfigurations = new HashSet<TableInfo.Index>(2);
        _indicesConfigurations.add(new TableInfo.Index("index_configurations_deviceId", false, Arrays.asList("deviceId"), Arrays.asList("ASC")));
        _indicesConfigurations.add(new TableInfo.Index("index_configurations_configKey", false, Arrays.asList("configKey"), Arrays.asList("ASC")));
        final TableInfo _infoConfigurations = new TableInfo("configurations", _columnsConfigurations, _foreignKeysConfigurations, _indicesConfigurations);
        final TableInfo _existingConfigurations = TableInfo.read(db, "configurations");
        if (!_infoConfigurations.equals(_existingConfigurations)) {
          return new RoomOpenHelper.ValidationResult(false, "configurations(com.iotlogic.blynk.data.local.entities.ConfigurationEntity).\n"
                  + " Expected:\n" + _infoConfigurations + "\n"
                  + " Found:\n" + _existingConfigurations);
        }
        final HashMap<String, TableInfo.Column> _columnsCommandQueue = new HashMap<String, TableInfo.Column>(15);
        _columnsCommandQueue.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("command", new TableInfo.Column("command", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("parameters", new TableInfo.Column("parameters", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("maxRetries", new TableInfo.Column("maxRetries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("result", new TableInfo.Column("result", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("errorMessage", new TableInfo.Column("errorMessage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("scheduledAt", new TableInfo.Column("scheduledAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("sentAt", new TableInfo.Column("sentAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCommandQueue.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCommandQueue = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCommandQueue.add(new TableInfo.ForeignKey("devices", "CASCADE", "NO ACTION", Arrays.asList("deviceId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCommandQueue = new HashSet<TableInfo.Index>(4);
        _indicesCommandQueue.add(new TableInfo.Index("index_command_queue_deviceId", false, Arrays.asList("deviceId"), Arrays.asList("ASC")));
        _indicesCommandQueue.add(new TableInfo.Index("index_command_queue_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesCommandQueue.add(new TableInfo.Index("index_command_queue_priority", false, Arrays.asList("priority"), Arrays.asList("ASC")));
        _indicesCommandQueue.add(new TableInfo.Index("index_command_queue_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoCommandQueue = new TableInfo("command_queue", _columnsCommandQueue, _foreignKeysCommandQueue, _indicesCommandQueue);
        final TableInfo _existingCommandQueue = TableInfo.read(db, "command_queue");
        if (!_infoCommandQueue.equals(_existingCommandQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "command_queue(com.iotlogic.blynk.data.local.entities.CommandQueueEntity).\n"
                  + " Expected:\n" + _infoCommandQueue + "\n"
                  + " Found:\n" + _existingCommandQueue);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "ad5c5e191c52a37f6895759b2e5025a7", "b78e8dae4023bd46679d566365d4fa98");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "devices","telemetry","configurations","command_queue");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `devices`");
      _db.execSQL("DELETE FROM `telemetry`");
      _db.execSQL("DELETE FROM `configurations`");
      _db.execSQL("DELETE FROM `command_queue`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DeviceDao.class, DeviceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TelemetryDao.class, TelemetryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ConfigurationDao.class, ConfigurationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommandQueueDao.class, CommandQueueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DeviceDao deviceDao() {
    if (_deviceDao != null) {
      return _deviceDao;
    } else {
      synchronized(this) {
        if(_deviceDao == null) {
          _deviceDao = new DeviceDao_Impl(this);
        }
        return _deviceDao;
      }
    }
  }

  @Override
  public TelemetryDao telemetryDao() {
    if (_telemetryDao != null) {
      return _telemetryDao;
    } else {
      synchronized(this) {
        if(_telemetryDao == null) {
          _telemetryDao = new TelemetryDao_Impl(this);
        }
        return _telemetryDao;
      }
    }
  }

  @Override
  public ConfigurationDao configurationDao() {
    if (_configurationDao != null) {
      return _configurationDao;
    } else {
      synchronized(this) {
        if(_configurationDao == null) {
          _configurationDao = new ConfigurationDao_Impl(this);
        }
        return _configurationDao;
      }
    }
  }

  @Override
  public CommandQueueDao commandQueueDao() {
    if (_commandQueueDao != null) {
      return _commandQueueDao;
    } else {
      synchronized(this) {
        if(_commandQueueDao == null) {
          _commandQueueDao = new CommandQueueDao_Impl(this);
        }
        return _commandQueueDao;
      }
    }
  }
}

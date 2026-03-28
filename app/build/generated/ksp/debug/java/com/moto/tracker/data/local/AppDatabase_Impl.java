package com.moto.tracker.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.ViewInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.moto.tracker.data.local.dao.DocumentDao;
import com.moto.tracker.data.local.dao.DocumentDao_Impl;
import com.moto.tracker.data.local.dao.ExpenseViewDao;
import com.moto.tracker.data.local.dao.ExpenseViewDao_Impl;
import com.moto.tracker.data.local.dao.FuelLogDao;
import com.moto.tracker.data.local.dao.FuelLogDao_Impl;
import com.moto.tracker.data.local.dao.KmLogDao;
import com.moto.tracker.data.local.dao.KmLogDao_Impl;
import com.moto.tracker.data.local.dao.MaintenanceLogDao;
import com.moto.tracker.data.local.dao.MaintenanceLogDao_Impl;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao;
import com.moto.tracker.data.local.dao.MaintenanceTaskDao_Impl;
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao;
import com.moto.tracker.data.local.dao.ManufacturerScheduleDao_Impl;
import com.moto.tracker.data.local.dao.ServiceAppointmentDao;
import com.moto.tracker.data.local.dao.ServiceAppointmentDao_Impl;
import com.moto.tracker.data.local.dao.VehicleDao;
import com.moto.tracker.data.local.dao.VehicleDao_Impl;
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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile VehicleDao _vehicleDao;

  private volatile DocumentDao _documentDao;

  private volatile MaintenanceTaskDao _maintenanceTaskDao;

  private volatile MaintenanceLogDao _maintenanceLogDao;

  private volatile KmLogDao _kmLogDao;

  private volatile FuelLogDao _fuelLogDao;

  private volatile ServiceAppointmentDao _serviceAppointmentDao;

  private volatile ManufacturerScheduleDao _manufacturerScheduleDao;

  private volatile ExpenseViewDao _expenseViewDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nickname` TEXT NOT NULL, `make` TEXT NOT NULL, `model` TEXT NOT NULL, `year` INTEGER NOT NULL, `type` TEXT NOT NULL, `vin` TEXT, `licensePlate` TEXT, `currentOdometer` INTEGER NOT NULL, `fuelType` TEXT NOT NULL, `color` TEXT, `purchaseDate` INTEGER, `imageUri` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `fileUri` TEXT, `thumbnailUri` TEXT, `expiryDate` INTEGER, `issueDate` INTEGER, `notes` TEXT, `amount` REAL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_vehicleId` ON `documents` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `maintenance_tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `taskType` TEXT NOT NULL, `customName` TEXT, `thresholdDays` INTEGER, `thresholdKm` INTEGER, `lastPerformedDate` INTEGER, `lastPerformedOdometer` INTEGER, `nextDueDate` INTEGER, `nextDueOdometer` INTEGER, `isActive` INTEGER NOT NULL, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_maintenance_tasks_vehicleId` ON `maintenance_tasks` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `maintenance_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskId` INTEGER NOT NULL, `vehicleId` INTEGER NOT NULL, `performedDate` INTEGER NOT NULL, `odometer` INTEGER NOT NULL, `cost` REAL, `mechanicName` TEXT, `serviceCenterName` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`taskId`) REFERENCES `maintenance_tasks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_maintenance_logs_taskId` ON `maintenance_logs` (`taskId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_maintenance_logs_vehicleId` ON `maintenance_logs` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `km_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `logDate` INTEGER NOT NULL, `odometer` INTEGER NOT NULL, `kmDriven` INTEGER NOT NULL, `notes` TEXT, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_km_logs_vehicleId` ON `km_logs` (`vehicleId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_km_logs_logDate` ON `km_logs` (`logDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fuel_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `fillDate` INTEGER NOT NULL, `odometer` INTEGER NOT NULL, `liters` REAL NOT NULL, `pricePerLiter` REAL NOT NULL, `totalCost` REAL NOT NULL, `fuelType` TEXT NOT NULL, `stationName` TEXT, `isTankFull` INTEGER NOT NULL, `kmPerLiter` REAL, `notes` TEXT, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fuel_logs_vehicleId` ON `fuel_logs` (`vehicleId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_fuel_logs_fillDate` ON `fuel_logs` (`fillDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `service_appointments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` INTEGER NOT NULL, `appointmentDate` INTEGER NOT NULL, `title` TEXT NOT NULL, `serviceCenterName` TEXT, `serviceCenterAddress` TEXT, `serviceCenterPhone` TEXT, `taskType` TEXT, `estimatedCost` REAL, `actualCost` REAL, `status` TEXT NOT NULL, `reminderEnabled` INTEGER NOT NULL, `reminderMinutesBefore` INTEGER NOT NULL, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_appointments_vehicleId` ON `service_appointments` (`vehicleId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_appointments_appointmentDate` ON `service_appointments` (`appointmentDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `manufacturer_schedules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `make` TEXT NOT NULL, `model` TEXT NOT NULL, `yearFrom` INTEGER NOT NULL, `yearTo` INTEGER, `taskType` TEXT NOT NULL, `taskLabel` TEXT NOT NULL, `intervalDays` INTEGER, `intervalKm` INTEGER, `notes` TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_manufacturer_schedules_make_model` ON `manufacturer_schedules` (`make`, `model`)");
        db.execSQL("CREATE VIEW `expense_view` AS SELECT vehicleId, 'fuel' as category, fillDate as date, totalCost as amount\n"
                + "        FROM fuel_logs\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'service' as category, performedDate as date, cost as amount\n"
                + "        FROM maintenance_logs WHERE cost IS NOT NULL\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'insurance' as category, issueDate as date, amount as amount\n"
                + "        FROM documents WHERE type = 'insurance' AND amount IS NOT NULL AND issueDate IS NOT NULL\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'other' as category, createdAt as date, amount as amount\n"
                + "        FROM documents WHERE type != 'insurance' AND amount IS NOT NULL");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4e8fe52da8e13d12714622dc7bf6fd6f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `vehicles`");
        db.execSQL("DROP TABLE IF EXISTS `documents`");
        db.execSQL("DROP TABLE IF EXISTS `maintenance_tasks`");
        db.execSQL("DROP TABLE IF EXISTS `maintenance_logs`");
        db.execSQL("DROP TABLE IF EXISTS `km_logs`");
        db.execSQL("DROP TABLE IF EXISTS `fuel_logs`");
        db.execSQL("DROP TABLE IF EXISTS `service_appointments`");
        db.execSQL("DROP TABLE IF EXISTS `manufacturer_schedules`");
        db.execSQL("DROP VIEW IF EXISTS `expense_view`");
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
        final HashMap<String, TableInfo.Column> _columnsVehicles = new HashMap<String, TableInfo.Column>(15);
        _columnsVehicles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("nickname", new TableInfo.Column("nickname", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("make", new TableInfo.Column("make", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("model", new TableInfo.Column("model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("vin", new TableInfo.Column("vin", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("licensePlate", new TableInfo.Column("licensePlate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("currentOdometer", new TableInfo.Column("currentOdometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("fuelType", new TableInfo.Column("fuelType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("color", new TableInfo.Column("color", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("purchaseDate", new TableInfo.Column("purchaseDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("imageUri", new TableInfo.Column("imageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVehicles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVehicles = new TableInfo("vehicles", _columnsVehicles, _foreignKeysVehicles, _indicesVehicles);
        final TableInfo _existingVehicles = TableInfo.read(db, "vehicles");
        if (!_infoVehicles.equals(_existingVehicles)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicles(com.moto.tracker.data.local.entity.VehicleEntity).\n"
                  + " Expected:\n" + _infoVehicles + "\n"
                  + " Found:\n" + _existingVehicles);
        }
        final HashMap<String, TableInfo.Column> _columnsDocuments = new HashMap<String, TableInfo.Column>(12);
        _columnsDocuments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("fileUri", new TableInfo.Column("fileUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("thumbnailUri", new TableInfo.Column("thumbnailUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("expiryDate", new TableInfo.Column("expiryDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("issueDate", new TableInfo.Column("issueDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("amount", new TableInfo.Column("amount", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDocuments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDocuments.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDocuments = new HashSet<TableInfo.Index>(1);
        _indicesDocuments.add(new TableInfo.Index("index_documents_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoDocuments = new TableInfo("documents", _columnsDocuments, _foreignKeysDocuments, _indicesDocuments);
        final TableInfo _existingDocuments = TableInfo.read(db, "documents");
        if (!_infoDocuments.equals(_existingDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "documents(com.moto.tracker.data.local.entity.DocumentEntity).\n"
                  + " Expected:\n" + _infoDocuments + "\n"
                  + " Found:\n" + _existingDocuments);
        }
        final HashMap<String, TableInfo.Column> _columnsMaintenanceTasks = new HashMap<String, TableInfo.Column>(14);
        _columnsMaintenanceTasks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("taskType", new TableInfo.Column("taskType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("customName", new TableInfo.Column("customName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("thresholdDays", new TableInfo.Column("thresholdDays", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("thresholdKm", new TableInfo.Column("thresholdKm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("lastPerformedDate", new TableInfo.Column("lastPerformedDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("lastPerformedOdometer", new TableInfo.Column("lastPerformedOdometer", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("nextDueDate", new TableInfo.Column("nextDueDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("nextDueOdometer", new TableInfo.Column("nextDueOdometer", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceTasks.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaintenanceTasks = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMaintenanceTasks.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMaintenanceTasks = new HashSet<TableInfo.Index>(1);
        _indicesMaintenanceTasks.add(new TableInfo.Index("index_maintenance_tasks_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoMaintenanceTasks = new TableInfo("maintenance_tasks", _columnsMaintenanceTasks, _foreignKeysMaintenanceTasks, _indicesMaintenanceTasks);
        final TableInfo _existingMaintenanceTasks = TableInfo.read(db, "maintenance_tasks");
        if (!_infoMaintenanceTasks.equals(_existingMaintenanceTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "maintenance_tasks(com.moto.tracker.data.local.entity.MaintenanceTaskEntity).\n"
                  + " Expected:\n" + _infoMaintenanceTasks + "\n"
                  + " Found:\n" + _existingMaintenanceTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsMaintenanceLogs = new HashMap<String, TableInfo.Column>(10);
        _columnsMaintenanceLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("taskId", new TableInfo.Column("taskId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("performedDate", new TableInfo.Column("performedDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("odometer", new TableInfo.Column("odometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("cost", new TableInfo.Column("cost", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("mechanicName", new TableInfo.Column("mechanicName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("serviceCenterName", new TableInfo.Column("serviceCenterName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMaintenanceLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMaintenanceLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMaintenanceLogs.add(new TableInfo.ForeignKey("maintenance_tasks", "CASCADE", "NO ACTION", Arrays.asList("taskId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMaintenanceLogs = new HashSet<TableInfo.Index>(2);
        _indicesMaintenanceLogs.add(new TableInfo.Index("index_maintenance_logs_taskId", false, Arrays.asList("taskId"), Arrays.asList("ASC")));
        _indicesMaintenanceLogs.add(new TableInfo.Index("index_maintenance_logs_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoMaintenanceLogs = new TableInfo("maintenance_logs", _columnsMaintenanceLogs, _foreignKeysMaintenanceLogs, _indicesMaintenanceLogs);
        final TableInfo _existingMaintenanceLogs = TableInfo.read(db, "maintenance_logs");
        if (!_infoMaintenanceLogs.equals(_existingMaintenanceLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "maintenance_logs(com.moto.tracker.data.local.entity.MaintenanceLogEntity).\n"
                  + " Expected:\n" + _infoMaintenanceLogs + "\n"
                  + " Found:\n" + _existingMaintenanceLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsKmLogs = new HashMap<String, TableInfo.Column>(7);
        _columnsKmLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("logDate", new TableInfo.Column("logDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("odometer", new TableInfo.Column("odometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("kmDriven", new TableInfo.Column("kmDriven", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKmLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysKmLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysKmLogs.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesKmLogs = new HashSet<TableInfo.Index>(2);
        _indicesKmLogs.add(new TableInfo.Index("index_km_logs_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        _indicesKmLogs.add(new TableInfo.Index("index_km_logs_logDate", false, Arrays.asList("logDate"), Arrays.asList("ASC")));
        final TableInfo _infoKmLogs = new TableInfo("km_logs", _columnsKmLogs, _foreignKeysKmLogs, _indicesKmLogs);
        final TableInfo _existingKmLogs = TableInfo.read(db, "km_logs");
        if (!_infoKmLogs.equals(_existingKmLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "km_logs(com.moto.tracker.data.local.entity.KmLogEntity).\n"
                  + " Expected:\n" + _infoKmLogs + "\n"
                  + " Found:\n" + _existingKmLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsFuelLogs = new HashMap<String, TableInfo.Column>(13);
        _columnsFuelLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("fillDate", new TableInfo.Column("fillDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("odometer", new TableInfo.Column("odometer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("liters", new TableInfo.Column("liters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("pricePerLiter", new TableInfo.Column("pricePerLiter", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("totalCost", new TableInfo.Column("totalCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("fuelType", new TableInfo.Column("fuelType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("stationName", new TableInfo.Column("stationName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("isTankFull", new TableInfo.Column("isTankFull", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("kmPerLiter", new TableInfo.Column("kmPerLiter", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFuelLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFuelLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFuelLogs.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFuelLogs = new HashSet<TableInfo.Index>(2);
        _indicesFuelLogs.add(new TableInfo.Index("index_fuel_logs_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        _indicesFuelLogs.add(new TableInfo.Index("index_fuel_logs_fillDate", false, Arrays.asList("fillDate"), Arrays.asList("ASC")));
        final TableInfo _infoFuelLogs = new TableInfo("fuel_logs", _columnsFuelLogs, _foreignKeysFuelLogs, _indicesFuelLogs);
        final TableInfo _existingFuelLogs = TableInfo.read(db, "fuel_logs");
        if (!_infoFuelLogs.equals(_existingFuelLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "fuel_logs(com.moto.tracker.data.local.entity.FuelLogEntity).\n"
                  + " Expected:\n" + _infoFuelLogs + "\n"
                  + " Found:\n" + _existingFuelLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsServiceAppointments = new HashMap<String, TableInfo.Column>(16);
        _columnsServiceAppointments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("vehicleId", new TableInfo.Column("vehicleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("appointmentDate", new TableInfo.Column("appointmentDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("serviceCenterName", new TableInfo.Column("serviceCenterName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("serviceCenterAddress", new TableInfo.Column("serviceCenterAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("serviceCenterPhone", new TableInfo.Column("serviceCenterPhone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("taskType", new TableInfo.Column("taskType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("estimatedCost", new TableInfo.Column("estimatedCost", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("actualCost", new TableInfo.Column("actualCost", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("reminderEnabled", new TableInfo.Column("reminderEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("reminderMinutesBefore", new TableInfo.Column("reminderMinutesBefore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceAppointments.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysServiceAppointments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysServiceAppointments.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesServiceAppointments = new HashSet<TableInfo.Index>(2);
        _indicesServiceAppointments.add(new TableInfo.Index("index_service_appointments_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        _indicesServiceAppointments.add(new TableInfo.Index("index_service_appointments_appointmentDate", false, Arrays.asList("appointmentDate"), Arrays.asList("ASC")));
        final TableInfo _infoServiceAppointments = new TableInfo("service_appointments", _columnsServiceAppointments, _foreignKeysServiceAppointments, _indicesServiceAppointments);
        final TableInfo _existingServiceAppointments = TableInfo.read(db, "service_appointments");
        if (!_infoServiceAppointments.equals(_existingServiceAppointments)) {
          return new RoomOpenHelper.ValidationResult(false, "service_appointments(com.moto.tracker.data.local.entity.ServiceAppointmentEntity).\n"
                  + " Expected:\n" + _infoServiceAppointments + "\n"
                  + " Found:\n" + _existingServiceAppointments);
        }
        final HashMap<String, TableInfo.Column> _columnsManufacturerSchedules = new HashMap<String, TableInfo.Column>(10);
        _columnsManufacturerSchedules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("make", new TableInfo.Column("make", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("model", new TableInfo.Column("model", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("yearFrom", new TableInfo.Column("yearFrom", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("yearTo", new TableInfo.Column("yearTo", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("taskType", new TableInfo.Column("taskType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("taskLabel", new TableInfo.Column("taskLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("intervalDays", new TableInfo.Column("intervalDays", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("intervalKm", new TableInfo.Column("intervalKm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManufacturerSchedules.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysManufacturerSchedules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesManufacturerSchedules = new HashSet<TableInfo.Index>(1);
        _indicesManufacturerSchedules.add(new TableInfo.Index("index_manufacturer_schedules_make_model", false, Arrays.asList("make", "model"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoManufacturerSchedules = new TableInfo("manufacturer_schedules", _columnsManufacturerSchedules, _foreignKeysManufacturerSchedules, _indicesManufacturerSchedules);
        final TableInfo _existingManufacturerSchedules = TableInfo.read(db, "manufacturer_schedules");
        if (!_infoManufacturerSchedules.equals(_existingManufacturerSchedules)) {
          return new RoomOpenHelper.ValidationResult(false, "manufacturer_schedules(com.moto.tracker.data.local.entity.ManufacturerScheduleEntity).\n"
                  + " Expected:\n" + _infoManufacturerSchedules + "\n"
                  + " Found:\n" + _existingManufacturerSchedules);
        }
        final ViewInfo _infoExpenseView = new ViewInfo("expense_view", "CREATE VIEW `expense_view` AS SELECT vehicleId, 'fuel' as category, fillDate as date, totalCost as amount\n"
                + "        FROM fuel_logs\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'service' as category, performedDate as date, cost as amount\n"
                + "        FROM maintenance_logs WHERE cost IS NOT NULL\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'insurance' as category, issueDate as date, amount as amount\n"
                + "        FROM documents WHERE type = 'insurance' AND amount IS NOT NULL AND issueDate IS NOT NULL\n"
                + "        UNION ALL\n"
                + "        SELECT vehicleId, 'other' as category, createdAt as date, amount as amount\n"
                + "        FROM documents WHERE type != 'insurance' AND amount IS NOT NULL");
        final ViewInfo _existingExpenseView = ViewInfo.read(db, "expense_view");
        if (!_infoExpenseView.equals(_existingExpenseView)) {
          return new RoomOpenHelper.ValidationResult(false, "expense_view(com.moto.tracker.data.local.entity.ExpenseView).\n"
                  + " Expected:\n" + _infoExpenseView + "\n"
                  + " Found:\n" + _existingExpenseView);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4e8fe52da8e13d12714622dc7bf6fd6f", "f7d0adc995e4dd653ce8c602efe3c612");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(1);
    final HashSet<String> _tables = new HashSet<String>(3);
    _tables.add("fuel_logs");
    _tables.add("maintenance_logs");
    _tables.add("documents");
    _viewTables.put("expense_view", _tables);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "vehicles","documents","maintenance_tasks","maintenance_logs","km_logs","fuel_logs","service_appointments","manufacturer_schedules");
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
      _db.execSQL("DELETE FROM `vehicles`");
      _db.execSQL("DELETE FROM `documents`");
      _db.execSQL("DELETE FROM `maintenance_tasks`");
      _db.execSQL("DELETE FROM `maintenance_logs`");
      _db.execSQL("DELETE FROM `km_logs`");
      _db.execSQL("DELETE FROM `fuel_logs`");
      _db.execSQL("DELETE FROM `service_appointments`");
      _db.execSQL("DELETE FROM `manufacturer_schedules`");
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
    _typeConvertersMap.put(VehicleDao.class, VehicleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DocumentDao.class, DocumentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MaintenanceTaskDao.class, MaintenanceTaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MaintenanceLogDao.class, MaintenanceLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(KmLogDao.class, KmLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FuelLogDao.class, FuelLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ServiceAppointmentDao.class, ServiceAppointmentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ManufacturerScheduleDao.class, ManufacturerScheduleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseViewDao.class, ExpenseViewDao_Impl.getRequiredConverters());
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
  public VehicleDao vehicleDao() {
    if (_vehicleDao != null) {
      return _vehicleDao;
    } else {
      synchronized(this) {
        if(_vehicleDao == null) {
          _vehicleDao = new VehicleDao_Impl(this);
        }
        return _vehicleDao;
      }
    }
  }

  @Override
  public DocumentDao documentDao() {
    if (_documentDao != null) {
      return _documentDao;
    } else {
      synchronized(this) {
        if(_documentDao == null) {
          _documentDao = new DocumentDao_Impl(this);
        }
        return _documentDao;
      }
    }
  }

  @Override
  public MaintenanceTaskDao maintenanceTaskDao() {
    if (_maintenanceTaskDao != null) {
      return _maintenanceTaskDao;
    } else {
      synchronized(this) {
        if(_maintenanceTaskDao == null) {
          _maintenanceTaskDao = new MaintenanceTaskDao_Impl(this);
        }
        return _maintenanceTaskDao;
      }
    }
  }

  @Override
  public MaintenanceLogDao maintenanceLogDao() {
    if (_maintenanceLogDao != null) {
      return _maintenanceLogDao;
    } else {
      synchronized(this) {
        if(_maintenanceLogDao == null) {
          _maintenanceLogDao = new MaintenanceLogDao_Impl(this);
        }
        return _maintenanceLogDao;
      }
    }
  }

  @Override
  public KmLogDao kmLogDao() {
    if (_kmLogDao != null) {
      return _kmLogDao;
    } else {
      synchronized(this) {
        if(_kmLogDao == null) {
          _kmLogDao = new KmLogDao_Impl(this);
        }
        return _kmLogDao;
      }
    }
  }

  @Override
  public FuelLogDao fuelLogDao() {
    if (_fuelLogDao != null) {
      return _fuelLogDao;
    } else {
      synchronized(this) {
        if(_fuelLogDao == null) {
          _fuelLogDao = new FuelLogDao_Impl(this);
        }
        return _fuelLogDao;
      }
    }
  }

  @Override
  public ServiceAppointmentDao serviceAppointmentDao() {
    if (_serviceAppointmentDao != null) {
      return _serviceAppointmentDao;
    } else {
      synchronized(this) {
        if(_serviceAppointmentDao == null) {
          _serviceAppointmentDao = new ServiceAppointmentDao_Impl(this);
        }
        return _serviceAppointmentDao;
      }
    }
  }

  @Override
  public ManufacturerScheduleDao manufacturerScheduleDao() {
    if (_manufacturerScheduleDao != null) {
      return _manufacturerScheduleDao;
    } else {
      synchronized(this) {
        if(_manufacturerScheduleDao == null) {
          _manufacturerScheduleDao = new ManufacturerScheduleDao_Impl(this);
        }
        return _manufacturerScheduleDao;
      }
    }
  }

  @Override
  public ExpenseViewDao expenseViewDao() {
    if (_expenseViewDao != null) {
      return _expenseViewDao;
    } else {
      synchronized(this) {
        if(_expenseViewDao == null) {
          _expenseViewDao = new ExpenseViewDao_Impl(this);
        }
        return _expenseViewDao;
      }
    }
  }
}

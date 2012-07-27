package co.gargoyle.supercab.android.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.util.Log;
import co.gargoyle.supercab.android.enums.FareType;
import co.gargoyle.supercab.android.model.Fare;
import co.gargoyle.supercab.android.model.PickupPoint;
import co.gargoyle.supercab.android.model.UserModel;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class SCOrmLiteHelper extends OrmLiteSqliteOpenHelper {
  
  public static final String DATABASE_NAME = "supercab.db";
  private static final int DATABASE_VERSION = 1;

  //private Dao<SMSEvent, Integer> smsEventDao = null;
  //private RuntimeExceptionDao<SMSEvent, Integer> smsEventRuntimeDao = null;

  public SCOrmLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * This is called when the database is first created. Usually you should call createTable statements here to create
   * the tables that will store your data.
   */
  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      Log.i(SCOrmLiteHelper.class.getName(), "onCreate");
      TableUtils.createTable(connectionSource, Fare.class);
      TableUtils.createTable(connectionSource, PickupPoint.class);
      TableUtils.createTable(connectionSource, UserModel.class);
    } catch (SQLException e) {
      Log.e(SCOrmLiteHelper.class.getName(), "Can't create database", e);
      throw new RuntimeException(e);
    }

    // here we try inserting data in the on-create as a test
    RuntimeExceptionDao<PickupPoint, Integer> dao = getRuntimeDao(PickupPoint.class);
    long millis = System.currentTimeMillis();
    // create some entries in the onCreate
    Address iHub = new Address(Locale.getDefault());
    iHub.setLatitude(-1.29885); 
    iHub.setLongitude(36.79089);
    iHub.setAddressLine(0,"iHub");

    PickupPoint pickup = new PickupPoint(FareType.PICKUP, iHub);
    dao.create(pickup);

    Address airport = new Address(Locale.getDefault());
    airport.setLatitude(-1.331);
    airport.setLongitude(36.9251);
    airport.setAddressLine(0, "Jomo Kenyatta Airport");
    PickupPoint destination = new PickupPoint(FareType.DROPOFF, airport);
    dao.create(destination);
    Log.i(SCOrmLiteHelper.class.getName(), "created new entries in onCreate: " + millis);
  }

  /**
   * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
   * the various data to match the new version number.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
      Log.i(SCOrmLiteHelper.class.getName(), "onUpgrade");
      TableUtils.dropTable(connectionSource, Fare.class, true);
      TableUtils.dropTable(connectionSource, PickupPoint.class, true);
      TableUtils.dropTable(connectionSource, UserModel.class, true);
      // after we drop the old databases, we create the new ones
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.e(SCOrmLiteHelper.class.getName(), "Can't drop databases", e);
      throw new RuntimeException(e);
    }
  }

  ///**
  // * Phone Topic
  // */
  //public Dao<PhoneTopic, Integer> getPhoneTopicDao() throws SQLException {
  //  if (phoneTopicDao == null) {
  //    phoneTopicDao = getDao(PhoneTopic.class);
  //  }
  //  return phoneTopicDao;
  //}

  //public RuntimeExceptionDao<PhoneTopic, Integer> getPhoneTopicRuntimeDao() {
  //  if (phoneTopicRuntimeDao == null) {
  //    phoneTopicRuntimeDao = getRuntimeExceptionDao(PhoneTopic.class);
  //  }
  //  return phoneTopicRuntimeDao;
  //}
  //
  ///**
  // * Phone Event
  // */
  //public Dao<PhoneEvent, Integer> getPhoneEventDao() throws SQLException {
  //  //if (phoneEventDao == null) {
  //  //  phoneEventDao = getDao(PhoneEvent.class);
  //  //}
  //  //return phoneEventDao;
  //  return getNewDao(PhoneEvent.class);
  //}

  //public RuntimeExceptionDao<PhoneEvent, Integer> getPhoneEventRuntimeDao() {
  //  if (phoneEventRuntimeDao == null) {
  //    phoneEventRuntimeDao = getRuntimeExceptionDao(PhoneEvent.class);
  //  }
  //  return phoneEventRuntimeDao;
  //}
  //
  ///**
  // * SMS Event
  // */
  //public Dao<SMSEvent, Integer> getSMSEventDao() throws SQLException {
  //  //if (smsEventDao == null) {
  //  //  smsEventDao = getDao(SMSEvent.class);
  //  //}
  //  //return smsEventDao;
  //  return getNewDao(SMSEvent.class);
  //}

  //public RuntimeExceptionDao<SMSEvent, Integer> getSMSEventRuntimeDao() {
  //  if (smsEventRuntimeDao == null) {
  //    smsEventRuntimeDao = getRuntimeExceptionDao(SMSEvent.class);
  //  }
  //  return smsEventRuntimeDao;
  //}

  ////////////////////////////////////////////////////////////
  // Generic Dao Getters
  ////////////////////////////////////////////////////////////

  @SuppressWarnings("rawtypes")
  private HashMap<Class, Dao> daoMap = new HashMap<Class, Dao>();
  
  @SuppressWarnings("unchecked")
  public <T> Dao<T, Integer> getMyDao(Class<T> clazz) throws SQLException {
    Dao<T, Integer> dao = daoMap.get(clazz);
    if (dao == null) {
      dao = getDao(clazz);
      daoMap.put(clazz, dao);
    }
    return dao;
  }
  
  @SuppressWarnings("rawtypes")
  private HashMap<Class, RuntimeExceptionDao> runtimeDaoMap = new HashMap<Class, RuntimeExceptionDao>();
  
  @SuppressWarnings("unchecked")
  public <T> RuntimeExceptionDao<T, Integer> getRuntimeDao(Class<T> clazz) {
    RuntimeExceptionDao<T, Integer> runtimeDao = runtimeDaoMap.get(clazz);
    if (runtimeDao == null) {
      runtimeDao = getRuntimeExceptionDao(clazz);
      runtimeDaoMap.put(clazz, runtimeDao);
    }
    return runtimeDao;
  }

  /**
   * Close the database connections and clear any cached DAOs.
   */
  @Override
  public void close() {
    super.close();
    daoMap.clear();
    runtimeDaoMap.clear();
//    phoneTopicRuntimeDao = null;
  }


}

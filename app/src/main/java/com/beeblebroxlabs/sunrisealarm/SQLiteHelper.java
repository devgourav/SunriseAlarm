package com.beeblebroxlabs.sunrisealarm;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by devgr on 08-Jul-17.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "SQLiteDatabase.db";
  public static final String TABLE_NAME = "ALARM_TIME";
  public static final String COLUMN_ALARM_ID = "ALARM_ID";
  public static final String COLUMN_ALARM_LABEL = "ALARM_LABEL";
  public static final String COLUMN_ALARM_HOUR = "ALARM_HOUR";
  public static final String COLUMN_ALARM_MINUTE = "ALARM_MINUTE";

  private SQLiteDatabase database;

  public SQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {

    db.execSQL("create table " + TABLE_NAME +
        " ( " + COLUMN_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        COLUMN_ALARM_LABEL + " VARCHAR, " +
        COLUMN_ALARM_HOUR + " INTEGER, " +
        COLUMN_ALARM_MINUTE + " INTEGER);");

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(db);
  }

  public void insertAlarmRecord(AlarmModel alarm) {
    database = this.getReadableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_ALARM_LABEL,alarm.getAlarmLabel());
    contentValues.put(COLUMN_ALARM_HOUR,alarm.getAlarmHour());
    contentValues.put(COLUMN_ALARM_MINUTE,alarm.getAlarmMinute());
    database.insert(TABLE_NAME, null, contentValues);
    database.close();
  }

  public void updateAlarmRecord(AlarmModel alarm) {
    database = this.getReadableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_ALARM_LABEL,alarm.getAlarmLabel());
    contentValues.put(COLUMN_ALARM_HOUR,alarm.getAlarmHour());
    contentValues.put(COLUMN_ALARM_MINUTE,alarm.getAlarmMinute());
    database.update(TABLE_NAME, contentValues, COLUMN_ALARM_ID + " = ?", new String[]{alarm.getAlarmId()});
    database.close();
  }

  public void deleteRecord(AlarmModel alarm) {
    database = this.getReadableDatabase();
    database.delete(TABLE_NAME, COLUMN_ALARM_ID + " = ?", new String[]{alarm.getAlarmId()});
    database.close();
  }

  public ArrayList<AlarmModel> getAllAlarmRecords() {
    database = this.getReadableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    ArrayList<AlarmModel> alarms = new ArrayList<AlarmModel>();
    AlarmModel alarmModel;
    if (cursor.getCount() > 0) {
      for (int i = 0; i < cursor.getCount(); i++) {
        cursor.moveToNext();
        alarmModel = new AlarmModel();
        alarmModel.setAlarmId(cursor.getString(0));
        alarmModel.setAlarmLabel(cursor.getString(1));
        alarmModel.setAlarmHour(cursor.getInt(2));
        alarmModel.setAlarmMinute(cursor.getInt(3));
        alarms.add(alarmModel);
      }
    }
    cursor.close();
    database.close();
    return alarms;
  }
}

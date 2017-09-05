package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

  private TextView clockText,alarmLabelText;
  private Button silentButton,snoozeButton;


  public static final int SNOOZE_TIME = 10*60*1000;//10 Minutes

  Intent alarmIntent,newAlarmIntent;
  SQLiteHelper sqLiteHelper;
  Boolean isMilitaryTimeFormat = FALSE;

  PendingIntent alarmPendingIntent;
  AlarmManager alarmManager;
  Calendar alarmTime = Calendar.getInstance();
  String alarmLabel,alarmUri;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    setContentView(R.layout.activity_alarm_ring);


    sqLiteHelper = new SQLiteHelper(this);
    Intent intent = getIntent();
    silentButton = (Button) findViewById(R.id.silentButton);
    snoozeButton = (Button) findViewById(R.id.snoozeButton);
    clockText = (TextView) findViewById(R.id.ringClockText);
    alarmLabelText = (TextView) findViewById(R.id.ringAlarmLabel);

    final int alarmHour = intent.getExtras().getInt("alarmHour");
    final int alarmMinute = intent.getExtras().getInt("alarmMinute");
    final String alarmId = intent.getExtras().getString("alarmId");



    if(intent.getExtras().containsKey("alarmLabel") && intent.getExtras().containsKey("alarmTone")){
      alarmLabel = intent.getExtras().getString("alarmLabel");
      alarmUri = intent.getExtras().getString("alarmTone");

    }else{
      alarmLabel=" ";
      alarmUri=" ";
    }
    Log.i("AlarmRing AlarmLabel:",alarmLabel);

    alarmLabelText.setText(alarmLabel);

    SharedPreferences timePreference = PreferenceManager.getDefaultSharedPreferences(this);
    isMilitaryTimeFormat = timePreference.getBoolean("24hourClock",FALSE);


    String formattedTime = checkTimeFormat(alarmHour,alarmMinute);
    clockText.setText(formattedTime);


    alarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);
    silentButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        alarmIntent.putExtra("isAlarmSet",Boolean.FALSE);
        sendBroadcast(alarmIntent);
        sqLiteHelper.deleteRecord(alarmId);

        Intent intent = new Intent(AlarmRingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    });

    newAlarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);
    snoozeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        newAlarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);

        newAlarmIntent.putExtra("alarmHour",alarmHour);
        newAlarmIntent.putExtra("alarmMinute",alarmMinute);
        newAlarmIntent.putExtra("alarmId",alarmId);
        newAlarmIntent.putExtra("alarmTone",alarmUri);
        newAlarmIntent.putExtra("alarmLabel",alarmLabel);
        newAlarmIntent.putExtra("isAlarmSet",Boolean.TRUE);
        alarmIntent.putExtra("isAlarmSet",Boolean.FALSE);
        sendBroadcast(alarmIntent);

        alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
        alarmTime.set(Calendar.MINUTE,alarmMinute);
        alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);

        Long timeInMillis = alarmTime.getTimeInMillis();

        Calendar newAlarmTime = Calendar.getInstance();
        newAlarmTime.setTimeInMillis(timeInMillis+SNOOZE_TIME);

        alarmPendingIntent = PendingIntent.getBroadcast(AlarmRingActivity.this,1,
            newAlarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,newAlarmTime.getTimeInMillis(),alarmPendingIntent);

        Intent intent = new Intent(AlarmRingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

      }
    });

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }

  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
  }

  public String checkTimeFormat(int alarmHour,int alarmMinute){
    if(isMilitaryTimeFormat){
      return String.format("%02d:%02d", alarmHour, alarmMinute);
    }else{
      return String.format("%02d:%02d %s", alarmHour % 12, alarmMinute,
          alarmHour < 12 ? "am" : "pm");
    }
  }


}
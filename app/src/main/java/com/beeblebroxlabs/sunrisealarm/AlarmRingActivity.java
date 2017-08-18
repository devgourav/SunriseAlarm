package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

  private TextView clockText;
  private TextView alarmLabelText;
  private Button silentButton;
  private Button snoozeButton;

  public static final int SNOOZE_TIME = 2;

  Intent alarmIntent;
  Intent newAlarmIntent;
  SQLiteHelper sqLiteHelper;
  Boolean is12hourClock = TRUE;

  PendingIntent alarmPendingIntent;
  AlarmManager alarmManager;
  Calendar alarmTime = Calendar.getInstance();
  String alarmLabel;
  String alarmUri;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Log.e("AlarmRingActivity:","onCreate");
    sqLiteHelper = new SQLiteHelper(this);

    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    alarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);
    newAlarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);


    setContentView(R.layout.activity_alarm_ring);
    silentButton = (Button) findViewById(R.id.silentButton);
    snoozeButton = (Button) findViewById(R.id.snoozeButton);
    clockText = (TextView) findViewById(R.id.ringClockText);
    alarmLabelText = (TextView) findViewById(R.id.ringAlarmLabel);

    final int alarmHour = intent.getExtras().getInt("alarmHour");
    final int alarmMinute = intent.getExtras().getInt("alarmMinute");
    final String alarmId = intent.getExtras().getString("alarmId");


    if(intent.getExtras().getString("alarmLabel") != null && intent.getExtras().getString("alarmTone") != null){
      alarmLabel = intent.getExtras().getString("alarmLabel");
      alarmUri = intent.getExtras().getString("alarmTone");
    }

    if(is12hourClock){
      int alarm12Hour = alarmHour % 12;
      clockText.setText(String.format("%02d:%02d %s", alarm12Hour, alarmMinute,
          alarmHour < 12 ? "am" : "pm"));
    }else{
      clockText.setText(String.format("%02d:%02d", alarmHour, alarmMinute));
    }


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

    snoozeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        newAlarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);



        newAlarmIntent.putExtra("alarmHour",alarmHour);
        newAlarmIntent.putExtra("alarmMinute",alarmMinute);
        newAlarmIntent.putExtra("alarmId",alarmId);
        newAlarmIntent.putExtra("alarmTone",alarmUri);
        newAlarmIntent.putExtra("isAlarmSet",Boolean.TRUE);

        alarmIntent.putExtra("isAlarmSet",Boolean.FALSE);
        sendBroadcast(alarmIntent);

        alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
        alarmTime.set(Calendar.MINUTE,alarmMinute + SNOOZE_TIME);
        alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);

        alarmPendingIntent = PendingIntent.getBroadcast(AlarmRingActivity.this,1,
            newAlarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime.getTimeInMillis(),alarmPendingIntent);

        System.out.println("alarmTime" + alarmTime);

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
}
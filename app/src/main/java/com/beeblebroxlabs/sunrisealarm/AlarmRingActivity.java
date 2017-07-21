package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmRingActivity extends AppCompatActivity {

  private TextView clockText;
  private Button silentButton;
  Intent alarmIntent;
  SQLiteHelper sqLiteHelper;
  Boolean is12hourClock = TRUE;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Log.e("AlarmRingActivity:","onCreate");
    sqLiteHelper = new SQLiteHelper(this);

    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    final Bundle extras = intent.getExtras();

    System.out.println("Bundle:" + extras.keySet());

    setContentView(R.layout.activity_alarm_ring);
    silentButton = (Button) findViewById(R.id.silentButton);
    clockText = (TextView) findViewById(R.id.ringClockText);

    int alarmHour = intent.getExtras().getInt("alarmHour");
    int alarmMinute = intent.getExtras().getInt("alarmMinute");


    clockText.setText(String.format("%02d:%02d", alarmHour,alarmMinute));


    if(is12hourClock){
      int alarm12Hour = alarmHour % 12;
      clockText.setText(String.format("%02d:%02d %s", alarm12Hour, alarmMinute,
          alarmHour < 12 ? "am" : "pm"));
    }else{
      clockText.setText(String.format("%02d:%02d", alarmHour, alarmMinute));
    }


    silentButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        alarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);
        alarmIntent.putExtra("isAlarmSet",Boolean.FALSE);
        sendBroadcast(alarmIntent);

        sqLiteHelper.deleteRecord(extras.getString("alarmId"));

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
package com.beeblebroxlabs.sunrisealarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AlarmRingActivity extends AppCompatActivity {

  private View mContentView;
  private Button silentButton;
  Intent alarmIntent;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    Log.e("AlarmRingActivity:","onCreate");

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_alarm_ring);
    silentButton = (Button) findViewById(R.id.silentButton);
    mContentView = findViewById(R.id.ringClockText);

    silentButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        alarmIntent = new Intent(AlarmRingActivity.this,AlarmReceiver.class);
        alarmIntent.putExtra("isAlarmSet",Boolean.FALSE);
        sendBroadcast(alarmIntent);
        Intent intent = new Intent(AlarmRingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    });


  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

  }
}
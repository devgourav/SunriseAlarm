package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import java.util.Arrays;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.e("AlarmReceiver:","onReceive");

    Boolean isAlarmSet = intent.getExtras().getBoolean("isAlarmSet");
    Intent alarmRingScreenIntent = new Intent(context,AlarmRingActivity.class);
    alarmRingScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Intent alarmRingtoneServiceIntent = new Intent(context, AlarmRingtonePlayingService.class);
    alarmRingtoneServiceIntent.putExtra("isAlarmSet",isAlarmSet);


    if(intent!=null){
      if(isAlarmSet) {
        context.startService(alarmRingtoneServiceIntent);
        context.startActivity(alarmRingScreenIntent);
      }else{
        context.stopService(alarmRingtoneServiceIntent);
      }
    }

  }
}

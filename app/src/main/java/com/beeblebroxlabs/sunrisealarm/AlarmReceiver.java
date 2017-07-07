package com.beeblebroxlabs.sunrisealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by devgr on 07-Jul-17.
 */

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.e("We are in the receiver.","Yay");
    Intent alarmServiceIntent = new Intent(context, AlarmRingtonePlayingService.class);
    context.startService(alarmServiceIntent);
  }
}

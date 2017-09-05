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

    //System.out.println("intent Extras:" + bundleToString(intent.getExtras()));
    Bundle extras = intent.getExtras();


    Intent alarmRingScreenIntent = new Intent(context,AlarmRingActivity.class);
    alarmRingScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    alarmRingScreenIntent.putExtras(extras);


    Intent alarmRingtoneServiceIntent = new Intent(context, AlarmRingtonePlayingService.class);
    alarmRingtoneServiceIntent.putExtras(extras);


    if(intent!=null){
      if(extras.getBoolean("isAlarmSet")) {
        context.startService(alarmRingtoneServiceIntent);
        context.startActivity(alarmRingScreenIntent);
      }else{
        context.stopService(alarmRingtoneServiceIntent);
      }
    }

  }
//  public static String bundleToString(Bundle bundle) {
//    StringBuilder out = new StringBuilder("Bundle[");
//
//    if (bundle == null) {
//      out.append("null");
//    } else {
//      boolean first = true;
//      for (String key : bundle.keySet()) {
//        if (!first) {
//          out.append(", ");
//        }
//
//        out.append(key).append('=');
//
//        Object value = bundle.get(key);
//
//        if (value instanceof int[]) {
//          out.append(Arrays.toString((int[]) value));
//        } else if (value instanceof byte[]) {
//          out.append(Arrays.toString((byte[]) value));
//        } else if (value instanceof boolean[]) {
//          out.append(Arrays.toString((boolean[]) value));
//        } else if (value instanceof short[]) {
//          out.append(Arrays.toString((short[]) value));
//        } else if (value instanceof long[]) {
//          out.append(Arrays.toString((long[]) value));
//        } else if (value instanceof float[]) {
//          out.append(Arrays.toString((float[]) value));
//        } else if (value instanceof double[]) {
//          out.append(Arrays.toString((double[]) value));
//        } else if (value instanceof String[]) {
//          out.append(Arrays.toString((String[]) value));
//        } else if (value instanceof CharSequence[]) {
//          out.append(Arrays.toString((CharSequence[]) value));
//        } else if (value instanceof Parcelable[]) {
//          out.append(Arrays.toString((Parcelable[]) value));
//        } else if (value instanceof Bundle) {
//          out.append(bundleToString((Bundle) value));
//        } else {
//          out.append(value);
//        }
//
//        first = false;
//      }
//    }
//
//    out.append("]");
//    return out.toString();
//  }
}

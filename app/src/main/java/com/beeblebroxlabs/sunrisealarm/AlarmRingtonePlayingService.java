package com.beeblebroxlabs.sunrisealarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by devgr on 07-Jul-17.
 */

public class AlarmRingtonePlayingService extends Service {

  MediaPlayer mediaPlayer = new MediaPlayer();
  private boolean mRunning;
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    mRunning = false;
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.e("RingtonePlayingService:","onStartCommand");

    Log.e("mRunning:",Boolean.toString(mRunning));
    Log.e("isAlarmSet:",Boolean.toString(intent.getExtras().getBoolean("isAlarmSet")));

    if(!mRunning && intent.getExtras().getBoolean("isAlarmSet")){
      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
      mediaPlayer = mediaPlayer.create(this,alarmSound);
      mediaPlayer.start();
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    mRunning = false;
    mediaPlayer.stop();
    mediaPlayer.reset();

  }
}

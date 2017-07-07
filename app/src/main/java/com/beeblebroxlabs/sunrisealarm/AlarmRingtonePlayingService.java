package com.beeblebroxlabs.sunrisealarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by devgr on 07-Jul-17.
 */

public class AlarmRingtonePlayingService extends Service {

  MediaPlayer mediaPlayer = new MediaPlayer();
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    mediaPlayer = mediaPlayer.create(this,alarmSound);
    mediaPlayer.start();

    return START_NOT_STICKY;
  }
}

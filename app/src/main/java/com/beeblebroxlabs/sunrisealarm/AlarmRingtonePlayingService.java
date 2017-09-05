package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.TRUE;

import android.Manifest.permission;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings.System;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.security.Permission;

/**
 * Created by devgr on 07-Jul-17.
 */

public class AlarmRingtonePlayingService extends Service {

  private MediaPlayer mediaPlayer;
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
    mediaPlayer = new MediaPlayer();

    if(!mRunning && intent.getExtras().getBoolean("isAlarmSet")){
      if(intent.getExtras().getString("alarmTone")!=null){
        String alarmPath = intent.getExtras().getString("alarmTone");
        Uri uri = Uri.parse(alarmPath);
        alarmPath = getRingtonePathFromContentUri(getApplicationContext(), uri);
        try {
          mediaPlayer.setDataSource(alarmPath);
          mediaPlayer.setLooping(TRUE);
          mediaPlayer.prepareAsync();
          mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
              mediaPlayer.start();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      else{

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
          mediaPlayer.setDataSource(this,uri);
          mediaPlayer.setLooping(TRUE);
          mediaPlayer.prepareAsync();
          mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
              mediaPlayer.start();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    mRunning = false;
    mediaPlayer.stop();
    mediaPlayer.reset();

  }

  //Change System generated path to the actual media path
  public static String getRingtonePathFromContentUri(Context context,
      Uri contentUri) {
    String[] proj = { MediaStore.Audio.Media.DATA };
    Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
        proj, null, null, null);
    ringtoneCursor.moveToFirst();

    String path = ringtoneCursor.getString(ringtoneCursor
        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

    ringtoneCursor.close();
    return path;
  }
}

package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlarmManager;
import android.support.v4.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.CalendarAlerts;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat.LongKey;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import java.io.File;
import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {
  public static final int REQUEST_STORAGE_ACCESS = 200;
  public static final int REQUEST_READ_EXTERNAL_STORAGE = 300;
  private static final String LOG_INFO = "LOG_INFO";
  private static final String LOG_ERROR = "LOG_ERROR";
  private static final Long DEFAULT_SUNRISE_TIME = 1468836000L;
  public static final int REQUEST_REPEAT_ALARM = 400;


  String alarmLabel;
  int alarmHour,alarmMinute;
  Boolean isSunriseTimeEnabledFlag=FALSE;
  Boolean isCustomTimeEnabledFlag=FALSE;
  Boolean isPickerTimeEnabledFlag=FALSE;

  Switch customSwitch,sunriseSwitch;

  Button setRingtoneButton;
  EditText alarmLabelEditText;
  TimePicker pickerTime;
  TextView sunriseTextView;


  PendingIntent alarmPendingIntent;
  AlarmManager alarmManager;
  Calendar alarmTime;

  Intent alarmIntent,ringtoneIntent;
  AlarmModel alarmModel;
  Uri alarmUri;
  String timeInMilis;
  SharedPreferences sharedPreferences;
  Calendar sunriseTime;
  String alarmId="";

  private static Context context;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_alarm);
    this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    SetAlarmActivity.context = getApplicationContext();


    sharedPreferences = getApplicationContext().getSharedPreferences("myPref",MODE_PRIVATE);

    if(sharedPreferences.contains("sunriseTime")){
      timeInMilis = sharedPreferences.getString("sunriseTime",DEFAULT_SUNRISE_TIME.toString());
    }else{
      Log.e(LOG_ERROR,"sunriseTime not set");
      timeInMilis = DEFAULT_SUNRISE_TIME.toString();
    }
    if(timeInMilis!=null){
      Long LongTimeInMilis = Long.parseLong(timeInMilis)*1000;//To covert 10 digit Unix time to 13 digit Unix Time
      sunriseTime = Calendar.getInstance();
      sunriseTime.setTimeInMillis(LongTimeInMilis);
    }else{
      Log.e(LOG_ERROR,"Could not set the sunrise Time");
    }

    setRingtoneButton = (Button)findViewById(R.id.setRingtoneButton);
    sunriseTextView = (TextView)findViewById(R.id.sunriseTimeText);
    sunriseTextView.setText("Sunrise is expected at "+ sunriseTime.get(Calendar.HOUR_OF_DAY)+":"+sunriseTime.get(Calendar.MINUTE)+" AM");

    checkWriteSettings();
    if(checkReadExternalStoragePermission()){
      setRingtoneButton.setEnabled(TRUE);
    }

    alarmTime = Calendar.getInstance();
    pickerTime = (TimePicker)findViewById(R.id.pickerTime);
    pickerTime.setEnabled(FALSE);


    alarmIntent = new Intent(SetAlarmActivity.this,AlarmReceiver.class);
    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    alarmLabelEditText = (EditText)findViewById(R.id.alarmLabelText);


    setRingtoneButton.setOnClickListener(new OnClickListener(){
      @Override
      public void onClick(View v) {
        String uri=null;
        ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone");
        if (uri != null) {
          ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,Uri.parse(uri));
        } else {
          ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,(Uri) null);
        }


        startActivityForResult(ringtoneIntent,REQUEST_STORAGE_ACCESS);}
    });



    sunriseSwitch = (Switch) findViewById(R.id.sunriseTimeToggle);
    sunriseSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSunriseTimeEnabled) {
        if (isSunriseTimeEnabled ) {
          Calendar currentTime = Calendar.getInstance();
          isSunriseTimeEnabledFlag = TRUE;

          alarmHour = sunriseTime.get(Calendar.HOUR_OF_DAY);
          alarmMinute = sunriseTime.get(Calendar.MINUTE);

          alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
          alarmTime.set(Calendar.MINUTE,alarmMinute);
          alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);

          if (alarmTime.compareTo(currentTime) <= 0) {
            // Today Set time passed, count to tomorrow
            alarmTime.add(Calendar.DATE, 1);
          }
          if(isCustomTimeEnabledFlag){
            customSwitch.setChecked(FALSE);
          }
        }else{
          isSunriseTimeEnabledFlag = FALSE;
        }
      }
    });


    customSwitch = (Switch) findViewById(R.id.customTimeToggle);
    customSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isCustomTimeEnabled) {
        if (isCustomTimeEnabled) {
          isCustomTimeEnabledFlag = TRUE;
          if(isCustomTimeEnabledFlag){
            sunriseSwitch.setChecked(FALSE);
            pickerTime.setEnabled(TRUE);
            pickerTime.setOnTimeChangedListener(new OnTimeChangedListener(){

              @Override
              public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(isCustomTimeEnabledFlag){
                  Calendar currentTime = Calendar.getInstance();
                  alarmHour = hourOfDay;
                  alarmMinute = minute;
                  alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
                  alarmTime.set(Calendar.MINUTE,alarmMinute);
                  alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);

                  alarmTime.set(Calendar.SECOND, 0);
                  alarmTime.set(Calendar.MILLISECOND, 0);

                  if (alarmTime.compareTo(currentTime) <= 0) {
                    // Today Set time passed, count to tomorrow
                    alarmTime.add(Calendar.DATE, 1);
                  }

                  isPickerTimeEnabledFlag = TRUE;
                }
              }});
          }
        }else{
          isCustomTimeEnabledFlag = FALSE;
          pickerTime.setEnabled(FALSE);

        }


      }
    });


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.set_alarm_menu, menu);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this);


    if (id == R.id.okButton) {
      if(isCustomTimeEnabledFlag || isSunriseTimeEnabledFlag){
        if(isPickerTimeEnabledFlag || isSunriseTimeEnabledFlag){

          alarmLabel = alarmLabelEditText.getText().toString();
          Log.i("SetAlarm AlarmLabel:",alarmLabel);

          alarmModel = new AlarmModel();
          alarmModel.setAlarmLabel(alarmLabel);
          alarmModel.setAlarmHour(alarmHour);
          alarmModel.setAlarmMinute(alarmMinute);
          alarmModel.setAlarmEnabled(TRUE);


          try {
            alarmId = sqLiteHelper.insertAlarmRecord(alarmModel);
          } catch (Exception e) {
            e.printStackTrace();
          }

          alarmIntent.putExtra("alarmHour",alarmHour);
          alarmIntent.putExtra("alarmMinute",alarmMinute);
          alarmIntent.putExtra("alarmLabel",alarmLabel);
          if(alarmUri!=null){
            alarmIntent.putExtra("alarmTone",alarmUri.toString());
          }
          alarmIntent.putExtra("isAlarmSet", TRUE);
          alarmIntent.putExtra("alarmId",alarmId);

          RepeatAlarmDialogFragment repeatAlarmDialogFragment = new RepeatAlarmDialogFragment();
          FragmentManager manager = getSupportFragmentManager();

          repeatAlarmDialogFragment.show(manager,"RepeatAlarm");

//          alarmPendingIntent = PendingIntent.getBroadcast(context,Integer.valueOf(alarmId),
//              alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
//          alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime.getTimeInMillis(),alarmPendingIntent);


//          Intent intent = new Intent(this, MainActivity.class);
//          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//          startActivity(intent);
        }else{
          Toast.makeText(this, "Please select the type of Alarm", Toast.LENGTH_SHORT).show();
        }
      }
      else{
        Toast.makeText(this, "Set a alarm time.", Toast.LENGTH_SHORT).show();
      }

    }
    if (id == R.id.cancelButton){
      Intent intent = new Intent(SetAlarmActivity.this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    if (resultCode == RESULT_OK && requestCode == REQUEST_STORAGE_ACCESS) {
//      alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
//      String alarmToneName = getFileName(alarmUri);
//      setRingtoneButton.setText("SET RINGTONE - " + alarmToneName);
//      if(alarmUri==null){
//        alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
//      }
//    }

    switch (requestCode) {
      case REQUEST_STORAGE_ACCESS:
        if (resultCode == RESULT_OK) {
          alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
          String alarmToneName = getFileName(alarmUri);
          setRingtoneButton.setText("SET RINGTONE - " + alarmToneName);
          if(alarmUri==null){
            alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI);
          }
        }
        break;
      case REQUEST_REPEAT_ALARM:
        if (resultCode == RESULT_OK) {
          alarmPendingIntent = PendingIntent.getBroadcast(context,Integer.valueOf(alarmId),
              alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
          alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
              alarmTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY,alarmPendingIntent);

          Intent intent = new Intent(this, MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);

        }
        else if( resultCode == RESULT_CANCELED){
          alarmPendingIntent = PendingIntent.getBroadcast(context,Integer.valueOf(alarmId),
              alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
          alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime.getTimeInMillis(),alarmPendingIntent);

          Intent intent = new Intent(this, MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);

        }
        break;
    }
  }

  private void checkWriteSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.System.canWrite(this)) {
        Log.i(LOG_INFO,"Permission granted");
      } else {
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch(requestCode) {
      case REQUEST_READ_EXTERNAL_STORAGE: {
        setRingtoneButton.setEnabled(TRUE);
      }
    }
  }

  private Boolean checkReadExternalStoragePermission(){
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
            REQUEST_READ_EXTERNAL_STORAGE);
        return FALSE;
      }else{
        return TRUE;
      }
    }
    return TRUE;
  }

  public String getFileName(Uri uri) {
    String result = null;
    if (uri.getScheme().equals("content")) {
      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
      try {
        if (cursor != null && cursor.moveToFirst()) {
          result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
      } finally {
        cursor.close();
      }
    }
    if (result == null) {
      result = uri.getPath();
      int cut = result.lastIndexOf('/');
      if (cut != -1) {
        result = result.substring(cut + 1);
      }
    }
    return result;
  }

  public static Context getAppContext(){
    return SetAlarmActivity.context;
  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

}

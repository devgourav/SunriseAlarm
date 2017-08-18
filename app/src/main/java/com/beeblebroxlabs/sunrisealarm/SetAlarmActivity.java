package com.beeblebroxlabs.sunrisealarm;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.CalendarContract.CalendarAlerts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import java.io.File;
import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {

  String alarmLabel;
  int alarmHour;
  int alarmMinute;
  Boolean isSunriseTimeEnabledFlag=Boolean.FALSE;
  Boolean isCustomTimeEnabledFlag=Boolean.FALSE;
  Boolean ispickerTimeEnabledFlag=Boolean.FALSE;
  Switch customSwitch,sunriseSwitch;

  Button setRingtoneButton;


  PendingIntent alarmPendingIntent;
  AlarmManager alarmManager;
  Calendar alarmTime = Calendar.getInstance();

  Intent alarmIntent;
  AlarmModel alarmModel = new AlarmModel();
  EditText alarmLabelText;
  TimePicker pickerTime;
  Uri alarmUri;
  Intent ringtoneIntent;

  public static final int REQUEST_STORAGE_ACCESS = 200;
  public static final int REQUEST_READ_EXTERNAL_STORAGE = 300;






  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_alarm);
    this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    pickerTime = (TimePicker)findViewById(R.id.pickerTime);
    pickerTime.setEnabled(Boolean.FALSE);
    alarmIntent = new Intent(SetAlarmActivity.this,AlarmReceiver.class);
    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    alarmLabelText = (EditText)findViewById(R.id.alarmLabelText);
    System.out.println("alarmLabelText:" + alarmLabelText);

    if(alarmLabelText.getText() != null){
      alarmLabel = alarmLabelText.getText().toString();
    }else{
      alarmLabel = " ";
    }

    checkWriteSettings();
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
          != PackageManager.PERMISSION_GRANTED) {

        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
            REQUEST_READ_EXTERNAL_STORAGE);
        return;
      }else{
        setRingtoneButton = (Button)findViewById(R.id.setRingtoneButton);

        setRingtoneButton.setOnClickListener(new OnClickListener(){
          @Override
          public void onClick(View v) {
            String uri=null;
            ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");


            if (uri != null) {
              ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,Uri.parse(uri));
            } else {
              ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,(Uri) null);
            }

            startActivityForResult(ringtoneIntent,REQUEST_STORAGE_ACCESS);}
        });
      }
    }





    sunriseSwitch = (Switch) findViewById(R.id.sunriseTimeToggle);
    sunriseSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSunriseTimeEnabled) {
        if (isSunriseTimeEnabled ) {
          Calendar currentTime = Calendar.getInstance();
          isSunriseTimeEnabledFlag = Boolean.TRUE;
          alarmHour = 6;
          alarmMinute = 30;

          alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
          alarmTime.set(Calendar.MINUTE,alarmMinute);
          alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);
          alarmTime.set(Calendar.SECOND, 0);
          alarmTime.set(Calendar.MILLISECOND, 0);

          if (alarmTime.compareTo(currentTime) <= 0) {
            // Today Set time passed, count to tomorrow
            alarmTime.add(Calendar.DATE, 1);
          }

          if(isCustomTimeEnabledFlag){
            customSwitch.setChecked(Boolean.FALSE);
          }
        }else{
          isSunriseTimeEnabledFlag = Boolean.FALSE;
        }
      }
    });


    customSwitch = (Switch) findViewById(R.id.customTimeToggle);
    customSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isCustomTimeEnabled) {
        if (isCustomTimeEnabled) {
          isCustomTimeEnabledFlag = Boolean.TRUE;
          if(isCustomTimeEnabledFlag){
            sunriseSwitch.setChecked(Boolean.FALSE);
            pickerTime.setEnabled(Boolean.TRUE);
            pickerTime.setOnTimeChangedListener(new OnTimeChangedListener(){

              @Override
              public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(isCustomTimeEnabledFlag){
                  Calendar currentTime = Calendar.getInstance();
                  alarmHour = hourOfDay;
                  alarmMinute = minute;
                  System.out.println("hourOfDay:"+hourOfDay+"minute:"+minute);
                  alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
                  alarmTime.set(Calendar.MINUTE,alarmMinute);
                  alarmTime.set(Calendar.AM_PM,alarmHour < 12 ? Calendar.AM : Calendar.PM);

                  alarmTime.set(Calendar.SECOND, 0);
                  alarmTime.set(Calendar.MILLISECOND, 0);

                  if (alarmTime.compareTo(currentTime) <= 0) {
                    // Today Set time passed, count to tomorrow
                    alarmTime.add(Calendar.DATE, 1);
                  }

                  ispickerTimeEnabledFlag = Boolean.TRUE;
                }
              }});
          }
        }else{
          isCustomTimeEnabledFlag = Boolean.FALSE;
          pickerTime.setEnabled(Boolean.FALSE);
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
    String alarmId;

    if (id == R.id.okButton) {
      if(isCustomTimeEnabledFlag || isSunriseTimeEnabledFlag){
        if(ispickerTimeEnabledFlag || isSunriseTimeEnabledFlag){




          alarmModel.setAlarmLabel(alarmLabel);
          alarmModel.setAlarmHour(alarmHour);
          alarmModel.setAlarmMinute(alarmMinute);
          alarmId = sqLiteHelper.insertAlarmRecord(alarmModel);


          System.out.println("alarmLabelSet:" + alarmLabel);


          alarmIntent.putExtra("alarmHour",alarmHour);
          alarmIntent.putExtra("alarmMinute",alarmMinute);
          alarmIntent.putExtra("alarmLabel",alarmLabel);
          System.out.println("alarmUri:"+alarmUri);
          if(alarmUri!=null){
            alarmIntent.putExtra("alarmTone",alarmUri.toString());
          }
          alarmIntent.putExtra("isAlarmSet",Boolean.TRUE);
          alarmIntent.putExtra("alarmId",alarmId);

          alarmPendingIntent = PendingIntent.getBroadcast(SetAlarmActivity.this,0,
              alarmIntent,PendingIntent.FLAG_CANCEL_CURRENT);
          alarmManager.set(AlarmManager.RTC_WAKEUP,alarmTime.getTimeInMillis(),alarmPendingIntent);


          Intent intent = new Intent(SetAlarmActivity.this, MainActivity.class);
          startActivity(intent);
        }else{
          Toast.makeText(this, "Please select the alarm time.", Toast.LENGTH_SHORT).show();
        }
      }
      else{
        Toast.makeText(this, "Please select the alarm time.", Toast.LENGTH_SHORT).show();
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
    if (resultCode == RESULT_OK && requestCode == REQUEST_STORAGE_ACCESS) {
      alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
      System.out.println("onActivityResult:"+alarmUri);
    }
  }

  private void checkWriteSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.System.canWrite(this)) {

        Log.e("Writepermission granted","d");

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
        setRingtoneButton = (Button)findViewById(R.id.setRingtoneButton);

        setRingtoneButton.setOnClickListener(new OnClickListener(){
          @Override
          public void onClick(View v) {
            String uri=null;
            ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");


            if (uri != null) {
              ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,Uri.parse(uri));
            } else {
              ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,(Uri) null);
            }

            startActivityForResult(ringtoneIntent,REQUEST_STORAGE_ACCESS);}
        });
      }
    }
  }


}

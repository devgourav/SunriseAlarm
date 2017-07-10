package com.beeblebroxlabs.sunrisealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {

  String alarmLabel = "";
  int alarmHour;
  int alarmMinute;
  Boolean isSunriseTimeEnabledFlag=Boolean.FALSE;
  Boolean isCustomTimeEnabledFlag=Boolean.FALSE;
  Switch customSwitch;
  Switch sunriseSwitch;
  String toastMessage;
  PendingIntent alarmPendingIntent;
  AlarmManager alarmManager;
  Calendar alarmTime = Calendar.getInstance();
  Intent alarmIntent;

  AlarmModel alarmModel = new AlarmModel();
  SQLiteHelper sqLiteHelper = new SQLiteHelper(this);



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_alarm);
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    final TimePicker pickerTime = (TimePicker)findViewById(R.id.pickerTime);

    pickerTime.setEnabled(Boolean.FALSE);

    alarmIntent = new Intent(SetAlarmActivity.this,AlarmReceiver.class);

    EditText alarmLabelText = (EditText)findViewById(R.id.alarmLabelText);
    alarmLabel = alarmLabelText.getText().toString();


    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);



    sunriseSwitch = (Switch) findViewById(R.id.sunriseTimeToggle);
    sunriseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSunriseTimeEnabled) {
        if (isSunriseTimeEnabled ) {
          isSunriseTimeEnabledFlag = Boolean.TRUE;
          alarmHour = 6;
          alarmMinute = 30;

          alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
          alarmTime.set(Calendar.MINUTE,alarmHour);

          if(isCustomTimeEnabledFlag){
            customSwitch.setChecked(Boolean.FALSE);
          }
        }else{
          isSunriseTimeEnabledFlag = Boolean.FALSE;
        }
      }
    });


    customSwitch = (Switch) findViewById(R.id.customTimeToggle);
    customSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isCustomTimeEnabled) {
        if (isCustomTimeEnabled) {
          isCustomTimeEnabledFlag = Boolean.TRUE;
          if(isCustomTimeEnabledFlag){
            sunriseSwitch.setChecked(Boolean.FALSE);
            pickerTime.setEnabled(Boolean.TRUE);
            pickerTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

              @Override
              public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(isCustomTimeEnabledFlag){
                  alarmHour = hourOfDay;
                  alarmMinute = minute;
                  alarmTime.set(Calendar.HOUR_OF_DAY,alarmHour);
                  alarmTime.set(Calendar.MINUTE,alarmHour);
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

    if (id == R.id.okButton) {
      if(!isCustomTimeEnabledFlag && !isSunriseTimeEnabledFlag){
        Toast.makeText(this, "Please select the alarm time.", Toast.LENGTH_SHORT).show();
      }
      else{
        alarmModel.setAlarmLabel(alarmLabel);
        alarmModel.setAlarmHour(alarmHour);
        alarmModel.setAlarmMinute(alarmMinute);

        sqLiteHelper.insertAlarmRecord(alarmModel);


        Intent intent = new Intent(SetAlarmActivity.this, MainActivity.class);
        startActivity(intent);
      }
    }
    if (id == R.id.cancelButton){
      Intent intent = new Intent(SetAlarmActivity.this, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    }
    return super.onOptionsItemSelected(item);
  }


}

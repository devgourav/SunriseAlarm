package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.TimePicker;
import com.beeblebroxlabs.sunrisealarm.AlarmDisplayFragment.OnFragmentInteractionListener;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * class:CustomAlarmListAdapter
 * method:getView()
 * This is used to display the custom list of alarms
 * */

public class CustomAlarmListAdapter extends ArrayAdapter<AlarmModel> {

  private final Activity activity;
  private final ArrayList<AlarmModel> alarmModels;
  Boolean isMilitaryTimeFormat = FALSE;

  public interface CustomSwitchListenerInterface
  {
    void onClick(String value);
  }
  CustomSwitchListenerInterface deleteSwitchListener;

  CustomSwitchListenerInterface disableSwitchListener;

  public CustomAlarmListAdapter(Activity activity, ArrayList<AlarmModel> alarmModels,
      CustomSwitchListenerInterface deleteSwitchListener,CustomSwitchListenerInterface disableSwitchListener) {
    super(activity, 0);
    this.activity = activity;
    this.alarmModels = alarmModels;
    this.deleteSwitchListener  = deleteSwitchListener;
    this.disableSwitchListener  = disableSwitchListener;
  }


  @Override
  public int getCount() {
    return (alarmModels == null) ? 0 : alarmModels.size();
  }

  public View getView(final int position, View view, ViewGroup parent) {
    LayoutInflater inflater = activity.getLayoutInflater();
    View rowView = inflater.inflate(R.layout.list_alarm, null, true);
    final Switch setAlarmSwitch = (Switch) rowView.findViewById(R.id.setAlarmSwitch);

    int alarmHour = alarmModels.get(position).getAlarmHour();
    int alarmMinute = alarmModels.get(position).getAlarmMinute();

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    isMilitaryTimeFormat = sharedPreferences.getBoolean("24hourClock",FALSE);

    String formattedTime = checkTimeFormat(alarmHour,alarmMinute);
    Calendar calendar = Calendar.getInstance();
    Calendar alarmCalendar = Calendar.getInstance();
    alarmCalendar.set(Calendar.HOUR_OF_DAY,alarmHour);
    alarmCalendar.set(Calendar.MINUTE,alarmMinute);



    long hourInMillis = 1000*60*60;
    long minuteInMillis = 1000*60;
    long alarmTimeInMillis = alarmCalendar.getTimeInMillis();
    long currentTimeInMillis = calendar.getTimeInMillis();
    long timeDifference;
    long hourLeft;
    long minuteLeft;



    if(alarmTimeInMillis>=currentTimeInMillis){
      timeDifference = alarmTimeInMillis-currentTimeInMillis;
    }
    else{
      alarmCalendar.add(Calendar.DATE,1);
      alarmTimeInMillis = alarmCalendar.getTimeInMillis();
      timeDifference = alarmTimeInMillis-currentTimeInMillis;
    }

    hourLeft = timeDifference/hourInMillis;
    timeDifference = timeDifference%hourInMillis;
    minuteLeft = timeDifference/minuteInMillis;



    String timeRemaining = " Alarm in "+hourLeft+" hours "+minuteLeft+" minutes";


    setAlarmSwitch.setText(formattedTime+timeRemaining);
    setAlarmSwitch.setChecked(TRUE);

    setAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSetAlarmSwitchEnabled) {
        if (!isSetAlarmSwitchEnabled) {
          if(disableSwitchListener != null){
            disableSwitchListener.onClick(alarmModels.get(position).getAlarmId());
            setAlarmSwitch.setChecked(FALSE);
          }
        }else{
          setAlarmSwitch.setChecked(TRUE);
        }
      }
    });

    setAlarmSwitch.setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
          if(deleteSwitchListener != null){
            deleteSwitchListener.onClick(alarmModels.get(position).getAlarmId());
            setAlarmSwitch.setChecked(TRUE);
            Vibrator vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
          }
        return false;
      }
    });
    return rowView;
  }




  public String checkTimeFormat(int alarmHour,int alarmMinute){
    if(isMilitaryTimeFormat){
      return String.format("%02d:%02d", alarmHour, alarmMinute);
    }else{
      return String.format("%02d:%02d %s", alarmHour % 12, alarmMinute,
          alarmHour < 12 ? "am" : "pm");
    }
  }
}




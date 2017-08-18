package com.beeblebroxlabs.sunrisealarm;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

  public interface AdapterInterface
  {
    void onClick(String value);
  }
  AdapterInterface deleteSwitchListener;

  public CustomAlarmListAdapter(Activity activity, ArrayList<AlarmModel> alarmModels,AdapterInterface deleteSwitchListener) {
    super(activity, 0);
    this.activity = activity;
    this.alarmModels = alarmModels;
    this.deleteSwitchListener  = deleteSwitchListener;
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
    Boolean is24hourClock = sharedPreferences.getBoolean("24hourClock",FALSE);

    if(is24hourClock){
      setAlarmSwitch.setText(String.format("%02d:%02d", alarmHour, alarmMinute));
    }else{
      int alarm12Hour = alarmHour % 12;
      setAlarmSwitch.setText(String.format("%02d:%02d %s", alarm12Hour, alarmMinute,
          alarmHour < 12 ? "am" : "pm"));
    }
    setAlarmSwitch.setChecked(TRUE);

    setAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSetAlarmSwitchEnabled) {
        if (!isSetAlarmSwitchEnabled) {
          if(deleteSwitchListener != null)
            deleteSwitchListener.onClick(alarmModels.get(position).getAlarmId());
            setAlarmSwitch.setChecked(TRUE);
        }
      }
    });


    return rowView;
  }
}




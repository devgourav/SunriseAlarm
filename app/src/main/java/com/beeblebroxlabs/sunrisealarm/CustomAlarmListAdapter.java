package com.beeblebroxlabs.sunrisealarm;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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


  public CustomAlarmListAdapter(Activity activity, ArrayList<AlarmModel> alarmModels) {
    super(activity, 0);
    this.activity = activity;
    this.alarmModels = alarmModels;
  }

  @Override
  public int getCount() {
    return (alarmModels == null) ? 0 : alarmModels.size();
  }

  public View getView(final int position, View view, ViewGroup parent) {
    LayoutInflater inflater = activity.getLayoutInflater();
    View rowView = inflater.inflate(R.layout.list_alarm, null, true);
    Switch setAlarmSwitch = (Switch) rowView.findViewById(R.id.setAlarmSwitch);

    setAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
      public void onCheckedChanged(CompoundButton buttonView, boolean isSetAlarmSwitchEnabled) {
        if (!isSetAlarmSwitchEnabled) {
          Log.e("Dialog box","Show Dialog box");
        }
      }
    });



    String alarmHour = Integer.toString(alarmModels.get(position).getAlarmHour());
    String alarmMinute = Integer.toString(alarmModels.get(position).getAlarmMinute());

    setAlarmSwitch.setText(alarmHour + ":" + alarmMinute);
    setAlarmSwitch.setChecked(Boolean.TRUE);

    return rowView;
  }
}



